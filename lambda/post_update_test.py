#!/usr/bin/env python
# encoding: utf-8

import unittest
import post_update

class PostUpdateTests(unittest.TestCase):

    def test_get_authorised_http(self):
        http = post_update.get_authorised_http()

    def test_integration(self):

        post_update.lambda_entry({
            "url": "https://www.theguardian.com/environment/live/2017/jan/19/global-warning-live-from-the-climate-change-frontline-as-trump-becomes-president",
            "amp": "https://amp.theguardian.com/environment/live/2017/jan/19/global-warning-live-from-the-climate-change-frontline-as-trump-becomes-president",
            "title": "Global Warning: 24 hours on the climate change frontline as Trump becomes president – as it happened",
            "created": "2017-01-19T07:03:18Z"
        },None )
        
    def test_atomise_url(self):

        liveHost = "https://www.theguardian.com"
        ampHost = "https://amp.theguardian.com"
        path = "/environment/live/2017/jan/19/global-warning-live-from-the-climate-change-frontline-as-trump-becomes-president"                
        body = post_update.atomise_url(
            "%s%s" % (ampHost, path),
            "%s%s" % (liveHost, path),
            "This is a title",
            "100Z"
        ).encode('utf-8')

        self.assertTrue("global-warning-live" in body)

        f = open("atom.xml", "w")
        f.write(body)
        f.close()

if __name__ == "__main__":
    unittest.main()
