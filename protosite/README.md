# protosite

Site prototype for Fantasy Baseball

## Things you can do

1. Run unit tests, rerunning automagically when files are saved (like conttest): `lein midje --lazytest` 
1. Run test server interactively: `lein ring server-headless 4000`.  Browse to `localhost:4000`.  Available URLs:
    1. /
    1. /example-static-file.html  # Example of static, rather than dynamic media.  Uses: CSS, JS, etc.
    1. Anything else # will give 404 not found error message

## Next things to try:

1. Deployment to cloud (Heroku, AWS, ...)
1. Connect w/ Datomic
1. User signup and login

## License

Copyright © 2013 Matthew Rocklin and John Jacobsen.  All rights reserved.
