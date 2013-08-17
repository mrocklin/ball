You can create a local or AWS VM.  `cd` to either `vbox` and/or `aws` and `vagrant up`, `vagrant provision`, `vagrant ssh`, ....

Provisioning an AWS Instance
----------------------------

For AWS provisioning to work, you'll want the following:

- `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` defined as environment variables
- `qball.pem` installed in `~/.ssh` with mode 0600.

When creating a new AWS instance, be sure to update `bin/hostname-aws` with the new hostname.

Connecting to the AWS Instance
------------------------------

`bin/ssh-aws` should work for `ssh`.

To `rsync` a directory, use `bin/rsync-aws`.  For example,

    bin/rsync-aws ../protosite /tmp/protosite

will rsync `protosite` to `/tmp/protosite` on the remote instance.

Clojure on the AWS Instance
---------------------------

On the VM you can, for example,

    lein new foo
    cd foo
    lein repl

Datomic should already be running.
