#/usr/bin/env python
# encoding: utf-8

from oauth2client.service_account import ServiceAccountCredentials
from jinja2 import Template
from datetime import datetime
import httplib2
import requests
import traceback
import os
import json

def atomise(url, liveurl, title, created, data):

    template = Template(open("posting.template.xml").read())
    now = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%SZ")
    created = datetime.fromtimestamp(int(created)/1000).strftime("%Y-%m-%dT%H:%M:%SZ")

    return template.render(
        feedTitle=unicode("The Guardian News", 'utf-8'),
        publisher=unicode("The Guardian", 'utf-8'),
        now8601Zulu=unicode(now, 'utf-8'),
        entryTitle=title,
        lastUpdatedDateIn8601Zulu=unicode(now, 'utf-8'),
        entryAMPUrl=url,
        entryLiveUrl=liveurl,
        pagedata=data
    )

def atomise_url(url, liveurl, title, created):

    print "Atomising %s" % url
    return atomise(url, liveurl, title, created, requests.get(url).text)

def get_authorised_http():

    SCOPES = [ "https://www.googleapis.com/auth/indexing" ]
    JSON_KEY_FILE = "keyfile.json"

    credentials = ServiceAccountCredentials.from_json_keyfile_name(
        JSON_KEY_FILE,
        scopes=SCOPES
    )

    return credentials.authorize(httplib2.Http())

def post_data(authedHttp, atomfeed):
    endpoint = os.getenv("GOOGLE_API", "https://indexing.googleapis.com/v1/index/public:update")
    response, content = authedHttp.request(endpoint, method="POST", body=atomfeed.encode('utf-8'))
    return response, content

def fail(event, msg):
    print msg
    return {
        "response": msg,
        "event": event
    }

def lambda_entry(event, context):

    print "Starting"

    try:

        body = json.loads(event["body"])

        resp, content = post_data(
            get_authorised_http(),
            atomise_url(
                body["amp"],
                body["url"],
                body["title"],
                body["created"]
            )
        )

        print "finished"

        return fail(event, str(resp))

    except Exception as err:

        traceback.print_exc()
        return fail(event, str(err))
