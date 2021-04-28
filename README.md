[![Build Status](http://cs321jenkins.servehttp.com/buildStatus/icon?job=SwipeAndShop)](http://cs321jenkins.servehttp.com/job/SwipeAndShop/)


# SwipeAndShop

## Dev
To make a release, make a commit with a tag like `git tag v0.0.2 -m 'init test release'`, changing `v0.0.2` to the next version. Jenkins will automatically build and upload the app to [GitHub releases](https://github.com/CS-321/SwipeAndShop/releases). The build status badge above indicates if such a build is successful or not.

When pushing, include `--tags` argument to `git` on command line explicitly: `git push origin master --tags`
