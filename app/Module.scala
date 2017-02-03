
import com.google.inject.AbstractModule
import services.{CapiFirehoseService, FirehoseService}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[FirehoseService]).to(classOf[CapiFirehoseService]).asEagerSingleton()
  }

}
