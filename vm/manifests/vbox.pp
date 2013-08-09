$user = 'vagrant'

user { "$user":
  ensure => present
}

