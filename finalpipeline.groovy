pipeline {
    agent {
        label 'k8s-master'
    }
    environment{
        DOCKER_CREDENTIALS=credentials("dockerhub-credentials")
        GITHUB_CREDENTIALS = credentials("github-credentials")
        IMAGE_TAG = "v${BUILD_NUMBER}"
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
                sh 'sudo docker build /home/ubuntu/jenkins/workspace/prodpipeline -t anil412/prodproject:${IMAGE_TAG}'
                sh 'sudo echo $DOCKER_CREDENTIALS_PSW | sudo docker login -u $DOCKER_CREDENTIALS_USR --password-stdin'
                sh 'sudo docker push anil412/prodproject:${IMAGE_TAG}'
            }
        }
        stage('Checkout K8S manifest SCM'){
            steps {
                git credentialsId: 'github-credentials', 
                url: 'https://github.com/anil-dixit/k8s-files.git',
                branch: 'main'
            }
        }
        stage('Update deploy.yaml') { 
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]){
                sh ''' 
                OLD_TAG=$(grep -oP 'anil412/prodproject:v\\d+' deployment.yaml | grep -oP 'v\\d+') 
                NEW_TAG=${IMAGE_TAG} 
                sed -i "s/${OLD_TAG}/${NEW_TAG}/g" deployment.yaml
                cat deployment.yaml
                git config user.name "${GIT_USER}" 
                git config user.email "abc@gmail.com"
                git add deployment.yaml 
                git commit -m 'Updated the deploy yaml | Jenkins Pipeline' 
                git remote -v 
                git push https://${GIT_USER}:${GIT_PASS}@github.com/anil-dixit/k8s-files.git HEAD:main
                ''' 
                }
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
