You can create a local or AWS VM.

For a local VM,

- `cd vbox`
- `vagrant up`
- `vagrant ssh`
- (to refresh after changing Puppet manifests, `vagrant provision`).

For an AWS VM,

- `cd aws`
- `vagrant up --provider=aws`.  For this to work, you'll want the following:
    - `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` defined as environment variables
    - `qball.pem` installed in `~/.ssh` with mode 0600.
- `vagrant ssh`
- `vagrant provision`  # as above

On the VM:

    lein new foo
    cd foo
    lein repl
