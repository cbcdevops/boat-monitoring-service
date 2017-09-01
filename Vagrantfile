Vagrant.configure("2") do |config|
	config.vm.define "db" do |db|
		db.vm.box = "centos/7"
		db.vm.hostname = 'cbsdev'
		db.vm.box_url = "geerlingguy/centos7"
		db.vm.post_up_message = "IMPORTANT MESSAGE(DB Host) : If the folder /vagrant_data is empty, then stop your VM using 'vagrant halt' command run 'vagrant plugin install vagrant-vbguest' command. Once it is completed run the vagrant up again."
		db.vm.network "private_network", type:"dhcp"
		db.vm.synced_folder ".", "/vagrant_data"
		db.vm.provider "virtualbox" do |vb|
			vb.gui = false
			vb.cpus = 2
			vb.memory = "2048"
		end
		db.vm.provision "shell", inline: <<-SHELL
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
			echo '************************************************************'
			echo '**************** Spinning up Cloudant Container ************'
			echo '************************************************************'
			docker run -d -v $(pwd) --name plcreadedev -p 8090:80 --restart=always ibmcom/cloudant-developer
		SHELL

		db.vm.provision "shell", inline: <<-SHELL, privileged: false
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

	config.vm.define "kube" do |kube|
		kube.vm.box = "flixtech/kubernetes"
		kube.vm.hostname = 'kubedev'
		kube.vm.box_url = "flixtech/kubernetes"
		kube.vm.network "private_network", type:"dhcp"
		kube.vm.provider "virtualbox" do |vb|
			vb.gui = false
			vb.cpus = 2
			vb.memory = "2048"
		end
		kube.vm.post_up_message = "route add 10.0.0.0 mask 255.255.255.0 10.10.0.2 # Windows"
	end
end