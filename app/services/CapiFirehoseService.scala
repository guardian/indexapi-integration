package services

import javax.inject._

import com.amazonaws.auth.{AWSCredentialsProviderChain, InstanceProfileCredentialsProvider, STSAssumeRoleSessionCredentialsProvider}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.gu.contentapi.firehose.ContentApiFirehoseConsumer
import com.gu.contentapi.firehose.kinesis.KinesisStreamReaderConfig

trait FirehoseService { }

@Singleton
class CapiFirehoseService @Inject()(playconfig: play.Configuration, consumerLogic: ConsumerLogicService) extends FirehoseService {

  val appName = playconfig.getString("appName")
  val roleArn = playconfig.getString("roleArn")
  val streamName = playconfig.getString("streamName")
  val regionName = playconfig.getString("regionName")
  val profileName = playconfig.getString("profileName")

  val dynamoChain = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider(profileName),
    new InstanceProfileCredentialsProvider()
  )

  val kinesisChain = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider(profileName),
    new STSAssumeRoleSessionCredentialsProvider.Builder(
      roleArn,
      profileName
    ).build()
  )

  val region = Region.getRegion(
    Regions.fromName(regionName)
  )

  val kinesisStreamReaderConfig = KinesisStreamReaderConfig(
    streamName = streamName,
    app = appName,
    stage = "PROD",
    mode = "live",
    suffix = None,
    kinesisCredentialsProvider = kinesisChain,
    dynamoCredentialsProvider = dynamoChain,
    awsRegion = regionName
  )

  val client: ContentApiFirehoseConsumer = new ContentApiFirehoseConsumer(
    kinesisStreamReaderConfig,
    consumerLogic
  )

  client.start()

}
