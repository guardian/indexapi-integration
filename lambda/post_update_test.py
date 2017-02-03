#!/usr/bin/env python
# coding=UTF-8

import unittest
import post_update

class PostUpdateTests(unittest.TestCase):

    def test_get_authorised_http(self):
        http = post_update.get_authorised_http()
    
    def test_atomise_url(self):

        liveHost = "https://www.theguardian.com"
        ampHost = "https://amp.theguardian.com"
        path = "/environment/live/2017/jan/19/global-warning-live-from-the-climate-change-frontline-as-trump-becomes-president"                
        body = post_update.atomise_url(
            "%s%s" % (ampHost, path),
            "%s%s" % (liveHost, path),
            "I'm a title I'm a title. I'm a title. Ay-ay-ay I'm a title"
        ).encode('utf-8')

        self.assertTrue("global-warning-live" in body)

        print body
        
        f = open("atom.xml", "w")
        f.write(body)
        f.close()

if __name__ == "__main__":
    unittest.main()
