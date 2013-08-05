# -*- mode: ruby -*-
# vi: set ft=ruby :
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.network :forwarded_port, guest: 80, host: 8080
  config.vm.network :forwarded_port, guest: 5000, host: 5000

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network :private_network, ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network :public_network

  # If true, then any SSH connections made will enable agent forwarding.
  config.ssh.forward_agent = true
  config.vm.provision :shell, :inline => "apt-get update -qq --fix-missing"
  config.vm.provision :shell, :inline => "apt-get install -qq -y puppet"
  config.vm.provision :puppet do |puppet|
    puppet.manifests_path = "../manifests"
    puppet.options = "--verbose --debug"
    puppet.manifest_file  = "common.pp"
  end
end
