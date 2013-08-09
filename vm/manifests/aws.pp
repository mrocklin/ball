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
  groups => ["staff", "root"],
  shell => "/bin/bash",
  home => "/home/$user",
  require => Group[$user]
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
  content => 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCB2lZaB64Ocqh4eYMf+ndmaIhoZyqaSun+vB26vqh8fTnEEg6nG0UY4Cz+GPrO9B6TFY5tXCSDfMSK+YzZtW1YIpj7rOiyU5NbmMqNS+pP8gciSORhuz8Dj2fPGkG+PGoLNF1LRJjdvaNwAxGeAUrlmFaj+N/f3aUbQG+P/j4Nxawy6+hd4kqUHOL87W3I0LaILa/tVfa+XkhGOGDCJTceFWS7rRqSQ/p17xkDoElLAlHUH0sCDTztnKoM/9KP4a32tDzo7BHvl11epkxjMdKuJNOIPSssqUczcyerPFkEAFvFVjJp/L4qKXljOzznH3UzHX2EE0qPh5Uepx3ZFbtv qball',
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
