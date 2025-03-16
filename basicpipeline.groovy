pipeline {
    agent {
        label 'k8s-master'
    }
    environment{
        DOCKER_CREDENTIALS=credentials("dockerhub-credentials")
    }
    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
        stage('Git'){
            steps{
                git branch: 'master' , url: 'https://github.com/anil-dixit/website.git'
            }
        }
        stage('Docker'){
            steps{
                sh 'sudo docker build /home/ubuntu/jenkins/workspace/prodpipeline -t anil412/prodproject:latest'
                sh 'sudo echo $DOCKER_CREDENTIALS_PSW | sudo docker login -u $DOCKER_CREDENTIALS_USR --password-stdin'
                sh 'sudo docker push anil412/prodproject:latest'
            }
        }
        stage('Kubernetes'){
		    steps{
				sh 'kubectl apply -f deployment.yaml'
				sh 'kubectl apply -f service.yaml'
			}
		}
    }
}
