You can create a local or AWS VM.  `cd` to either `vbox` and/or `aws` and `vagrant up`, `vagrant provision`, `vagrant ssh`, ....

For AWS provisioning to work, you'll want the following:

- `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` defined as environment variables
- `qball.pem` installed in `~/.ssh` with mode 0600.

When connecting to an AWS VM, note that `vagrant ssh` puts you in the
`ubuntu` account; you'll want to `sudo su - vagrant`. Or you can `ssh
vagrant@FILL_ME_IN.compute.amazonaws.com -p 22 -o
DSAAuthentication=yes -o LogLevel=FATAL -o StrictHostKeyChecking=no -o
UserKnownHostsFile=/dev/null -o IdentitiesOnly=yes -i
$HOME/.ssh/qball.pem -o ForwardAgent=yes`. `FILL_ME_IN` is whatever
AWS address which was assigned when the instance was created. Note
that https://github.com/Instagram/ec2-ssh is very helpful for pulling
down AWS long names from the "short" name assigned to your instance
(usually, `qball`).  As an example,

    $  ec2-host qball
    ec2-54-213-101-195.us-west-2.compute.amazonaws.com


On the VM:

    lein new foo
    cd foo
    lein repl
