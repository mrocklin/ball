group { "puppet": ensure => "present", }

File { owner => 0, group => 0, mode => 0644 }

file { '/etc/motd': content => "Fantasy Baseball In A Box\n" }

package { 'default-jre': ensure => installed }

# Leiningen / Clojure

$user = 'vagrant'

file { "leiningen/create-local-bin-folder":
  ensure => directory,
  path => "/home/$user/bin",
  owner => $user,
  group => $user,
  mode => '755',
}

$lein_url = "https://github.com/technomancy/leiningen/raw/stable/bin/lein"

exec { "leiningen/install-script":
  user => $user,
  group => $user,
  path => ["/bin", "/usr/bin", "/usr/local/bin"],
  cwd => "/home/$user/bin",
  command => "wget ${lein_url} && chmod 755 lein",
  creates => ["/home/$user/bin/lein",
              "/home/$user/.lein"],
}
