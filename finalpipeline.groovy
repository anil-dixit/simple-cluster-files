pipeline {
    agent {
        label 'test'
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
                git branch: 'develop' , url: 'https://github.com/anil-dixit/website.git'
            }
        }
        stage('Docker'){
            steps{
                sh 'sudo docker build /home/ubuntu/workspace/job1 -t anil412/prodproject:latest'
                sh 'sudo docker run -itd -p 81:80 --name=c1 anil412/prodproject:latest'
            }
        }

    }
}
