# protosite

Site prototype for Fantasy Baseball

## Things you can do

1. Run unit tests, rerunning automagically when files are saved (like
conttest): `lein midje --lazytest`. This will work on the Vagrant VM if you `cd /site` first.
1. Run test server interactively: `lein ring server-headless 5000`.
Browse to `localhost:5000`. Also works with server running on Vagrant
VM -- you can browse to your laptop's port 5000 and it will forward to
the VM. Available URLs:
    1. /
    1. /example-static-file.html  # Example of static, rather than dynamic media.  Uses: CSS, JS, etc.
    1. Anything else # will give 404 not found error message

## Next things to try:

1. Finish connecting w/ Datomic
1. User signup and login
1. Deployment to cloud (Heroku, AWS, ...)

## License

Copyright © 2013 Matthew Rocklin and John Jacobsen.  All rights reserved.
