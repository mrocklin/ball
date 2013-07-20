#!/bin/bash

mkdir -p /home/vagrant/bin
wget -q https://raw.github.com/technomancy/leiningen/stable/bin/lein -O /home/vagrant/bin/lein
chmod 0755 /home/vagrant/bin/lein
