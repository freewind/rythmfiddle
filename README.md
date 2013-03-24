Use Play 1.2.5

How to run
----------

1. `play deps --sync`
1. go to module `app-base`, and delete the file `UADetector.java`
1. `cp conf/account.txt.sample conf/account.txt`
1. add `username=password` list there
1. `play run`
1. visit `http://localhost:9000`