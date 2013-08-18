$user = 'vagrant'

group { $user:
  ensure => present
}

file { "/home/$user":
  ensure => directory,
  owner => $user,
  group => $user,
  mode => '755',
  require => User[$user]
}

user { $user:
  ensure => present,
  gid => $user,
  groups => ["staff", "root", "admin"],
  shell => "/bin/bash",
  home => "/home/$user",
  require => Group[$user]
}

file { "/etc/sudoers.d/91-sudoers-vagrant":
  ensure => file,
  owner => "root",
  group => "root",
  mode => '440',
  content => "vagrant ALL=(ALL) NOPASSWD:ALL\n"
}

file { "/home/$user/.ssh":
  ensure => directory,
  owner => $user,
  group => $user,
  mode => '700',
  require => File["/home/$user"]
}

file { "/home/$user/.ssh/authorized_keys":
  ensure => file,
  owner => $user,
  group => $user,
  mode => '600',
  content => 'INSERT_RSA_KEY_HERE',
  require => File["/home/$user/.ssh"]
}

file { "/home/$user/.bash_profile":
  ensure => file,
  owner => $user,
  group => $user,
  mode => '644',
  content => '# if running bash
if [ -n "$BASH_VERSION" ]; then
    # include .bashrc if it exists
    if [ -f "$HOME/.bashrc" ]; then
	. "$HOME/.bashrc"
    fi
fi

if [ -d "$HOME/bin" ] ; then
    PATH="$HOME/bin:$PATH"
fi'
}
