# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://vagrantcloud.com/search.
  config.vm.box = "centos/7"
  config.vm.hostname="cbsdev"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # NOTE: This will enable public access to the opened port
  # config.vm.network "forwarded_port", guest: 8001, host: 8001
   
   config.vm.post_up_message = "IMPORTANT MESSAGE : If the folder /vagrant_data is empty, then stop your VM using 'vagrant halt' command run 'vagrant plugin install vagrant-vbguest' command. Once it is completed run the vagrant up again."

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine and only allow access
  # via 127.0.0.1 to disable public access
  # config.vm.network "forwarded_port", guest: 8001, host: 8090, host_ip: "127.0.0.1"

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"
	config.vm.network "private_network", type:"dhcp"
  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  config.vm.synced_folder ".", "/vagrant_data"

  #To Copy the Mongo repo file
  config.vm.provision "file", source: "mongodb-enterprise.repo", destination: "/tmp/mongodb-enterprise.repo"
  config.vm.provision "file", source: "mongo.conf", destination: "/tmp/mongo.conf"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
   config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
	 vb.cpus = 3
     vb.memory = "4096"
   end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
   config.vm.provision "shell", inline: <<-SHELL 
    sudo yum check-update
    sudo yum makecache fast	
    echo '************************************************************'
    echo '**************** Installing GIT ****************************'
	echo '************************************************************'	
    sudo yum install -y git
	sudo yum install -y maven	
	if yum list installed docker
    then
    echo '************************************************************'
    echo '**************** Removing old version of Docker ************'
	echo '************************************************************'	
	  sudo systemctl stop docker
      sudo yum remove docker \
                 docker-common \
                 docker-selinux \
                 docker-engine
    fi
    if yum list installed docker-ce-stable
    then
    echo '###### INFO: Docker already installed ####################'
    else
	echo '************************************************************'
    echo '*** Installing latest version of Docker Community Edition *****'
	echo '************************************************************'	      
      sudo yum install -y yum-utils device-mapper-persistent-data lvm2 
	  sudo curl -fsSL https://get.docker.com/ | sh
      systemctl start docker
      systemctl status docker
      systemctl enable docker
    fi
	echo '###### INFO: Adding Vagrant to Docker ######################'
    sudo usermod -aG docker vagrant	
    echo '************************************************************'
    echo '**************** Installing Bluemix CLI ********************'
	echo '************************************************************'	
    sudo curl -fsSL https://clis.ng.bluemix.net/install/linux | sh
	echo '************************************************************'
    echo '**************** Installing kubectl ************************'
	echo '************************************************************'		
    curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.7.0/bin/linux/amd64/kubectl
	chmod +x ./kubectl
	mv ./kubectl /usr/local/bin/kubectl
	#echo '************************************************************'
    #echo '**************** Spinning mongoDB 3.4 @ 27010 **************'
    #echo '************************************************************'
    #sudo cp /tmp/mongodb-enterprise.repo /etc/yum.repos.d/
    #sudo mkdir /data
    #sudo mkdir /data/db
    #sudo yum install -y mongodb-enterprise
    #sudo rm -rf /etc/mongod.conf
    #sudo cp /mongod.conf /etc/
	#sudo docker run -d -p 27017:27017 --restart=always --name mongo-dev  mongo
	#echo '************************************************************'
    #echo '**************** Spinning rancher @8080  *******************'
    #echo '************************************************************'
	#sudo docker run -d --restart=unless-stopped -p 8080:8080 rancher/server
	echo '************************************************************'
    echo '**************** Spinning up Cloudant Container ************'
    echo '************************************************************'
	docker run -d -v $(pwd) --name plcreadedev -p 8090:80 --restart=always ibmcom/cloudant-developer
	#echo '**************** Installing wget ************'
	#sudo yum install -y wget
	#sudo yum remove VirtualBox*
	#sudo cd /etc/yum.repos.d/
	#sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo
	#sudo yum update
	#sudo yum install binutils qt gcc make patch libgomp glibc-headers glibc-devel kernel-headers kernel-devel dkms
	#yum install VirtualBox-7.3
	#sudo usermod -a -G vboxusers vagrant
	SHELL
	
	config.vm.provision "shell", inline: <<-SHELL, privileged: false
    PLUGINS=$(bx plugin list)
    if echo $PLUGINS | grep dev
    then
      bx plugin update dev -r Bluemix
    else
	  echo '************************************************************'
      echo '************** Installing Bluemix dev plugin ***************'
	  echo '************************************************************'
      bx plugin install dev -r Bluemix
    fi
    if echo $PLUGINS | grep container-service
    then
      bx plugin update container-service -r Bluemix
    else
	  echo '************************************************************' 	
      echo '*********** Installing Bluemix container-service plugin ****'
	  echo '************************************************************'
      bx plugin install container-service -r Bluemix
    fi
    
    if echo $PLUGINS | grep container-registry
    then
      bx plugin update container-registry -r Bluemix
    else
	  echo '************************************************************'
      echo '******** Installing Bluemix container-registry plugin ******'
	  echo '************************************************************'
      bx plugin install container-registry -r Bluemix
    fi		
    SHELL
  
end
