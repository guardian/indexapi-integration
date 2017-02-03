#/usr/bin/env python

from oauth2client.service_account import ServiceAccountCredentials
from jinja2 import Template
from datetime import datetime
import httplib2
import requests
import os

def atomise(url, liveurl, title, created, data):

    template = Template(open("posting.template.xml").read())
    now = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%SZ")
    
    return template.render(
        feedTitle="The Guardian News",
        publisher="The Guardian",
        now8601Zulu=now,
        entryTitle=title,
        lastUpdatedDateIn8601Zulu=now,
        entryAMPUrl=url,
        entryLiveUrl=liveurl,
        pagedata=data
    )

def atomise_url(url, liveurl, title, created):

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

    endpoint = "https://indexing.googleapis.com/v1/index/public:update"
    response, content = authedHttp.request(endpoint, method="POST", body=atomfeed.encode('utf-8'))
    return response, content
    
def lambda_entry(event, context):

    if "url" not in event:
        return {
            "request": event,
            "response": "This doesn't look like a valid message"
        }

    if os.getenv("INTEGRATION_ON", "no") != "yes":
            return {
            "request": event,
            "response": "integration is not enabled"
        }

    try:
        
        resp, content = post_data(
            get_authorised_http(),
            atomise_url(
                event["url"],
                event["amp"],
                event["title"],
                event["created"]
            )
        )
    
        return {
            "request": event,
            "response": resp
        }

    except:

        return {
            "request": event,
            "response": "failed"
        }

