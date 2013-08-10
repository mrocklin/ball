group { "puppet": ensure => "present", }

File { owner => 0, group => 0, mode => 0644 }

file { '/etc/motd': content => "Fantasy Baseball In A Box\n" }

package { 'default-jre': ensure => installed }
package { 'unzip': ensure => installed }
package { 'upstart': ensure => installed }
package { 'git-core': ensure => installed }
package { 'tmux': ensure => installed }
package { 'curl': ensure => installed }

# Leiningen / Clojure:

$user = 'vagrant'  # Fixme - make this shared somehow
user { "$user":    # "                              "
  ensure => present
}

file { "/home/$user":
  ensure => directory,
  owner => $user,
  group => $user,
  mode => '755',
  require => User[$user]
}

file { "/home/$user/bin":
  ensure => directory,
  owner => $user,
  group => $user,
  mode => '755',
  require => File["/home/$user"]
}

$lein_url = "https://github.com/technomancy/leiningen/raw/stable/bin/lein"

file { "/home/$user/.lein":
  ensure => directory,
  path => "/home/$user/.lein",
  owner => $user,
  group => $user,
  require => File["/home/$user/bin"]
}


file { "leiningen/create-plugins-dir":
  ensure => file,
  path => "/home/$user/.lein/profiles.clj",
  owner => $user,
  group => $user,
  mode => '755',
  content => '{:user {:plugins [[lein-midje "3.0.0"]]}}',
  require => User[$user]
}

exec { "leiningen/install-script":
  user => $user,
  path => ["/bin", "/usr/bin", "/usr/local/bin"],
  cwd => "/home/$user/bin",
  command => "wget ${lein_url} && chmod 755 lein",
  require => User[$user],
  creates => ["/home/$user/bin/lein"]
}

# Datomic stuff - see
# http://vaughndickson.com/2012/11/20/deploying-datomic-free-on-ec2-or-any-ubuntu-system/

file { "datomic/home":
  ensure => directory,
  path => "/var/lib/datomic",
  owner => datomic,
  mode => '755',
  require => User["datomic"]
}
 
user { "datomic":
  ensure => present,
  shell => "/bin/bash",
  home => "/var/lib/datomic"
}

file { "datomic/data":
  ensure => directory,
  path => "/var/lib/datomic/data",
  owner => datomic,
  require => User["datomic"],
  mode => '755'
}
 
# # Version from http://downloads.datomic.com/free.html
$datomic_version = "0.8.4020.26"
$datomic_base = "datomic-free-${datomic_version}"
$datomic_file = "${datomic_base}.zip"
$datomic_url = "http://downloads.datomic.com/${datomic_version}/${datomic_file}"

file { "/var/lib/datomic/transactor.properties":
  owner => "datomic",
  ensure => present,
  content => "########### free mode config ###############
protocol=free
#<PRIVATE IP or 127.0.0.1 if accessing from same host only>:
host=127.0.0.1
# free mode will use 3 ports starting with this one:
port=4334
 
## optional overrides if you don't want ./data and ./log
data-dir=/var/lib/datomic/data/
log-dir=/var/log/datomic/
"
}

exec { "datomic/install-script":
  user => "datomic",
  path => ["/bin", "/usr/bin", "/usr/local/bin"],
  cwd => "/var/lib/datomic",
  require => User["datomic"],
  command => "wget ${datomic_url} && unzip -o ${datomic_file} && ln -s ${datomic_base} runtime",
  creates => ["/var/lib/datomic/${datomic_file}",
              "/var/lib/datomic/runtime"]
}

file { "/etc/init/datomic.conf":
  owner => "root",
  ensure => present,
  require => User["datomic"],
  content => "start on runlevel [2345]
 
pre-start script
bash << \"EOF\"
mkdir -p /var/log/datomic
chown -R datomic /var/log/datomic
EOF
end script
 
start on (started network-interface
or started network-manager
or started networking)
 
stop on (stopping network-interface
or stopping network-manager
or stopping networking)
 
respawn
 
script
exec su - datomic -c 'cd /var/lib/datomic/runtime; /var/lib/datomic/runtime/bin/transactor -Xms100m -Xmx500m /var/lib/datomic/transactor.properties 2>&1 >> /var/log/datomic/datomic.log'
end script
 
stop on runlevel [016]"
}

service { "datomic":
  enable => true,
  ensure => running,
  provider => 'upstart',
  require => File['/etc/init/datomic.conf',
                  '/var/lib/datomic/transactor.properties'],
}
