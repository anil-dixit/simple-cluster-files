# main.tf
provider "aws" {
  region     = var.aws_region
}
resource "aws_instance" "kubernetes-slave1" {
  tags = {
    Name = "Kubernetes-Slave1-test"
  }
  key_name      = var.key_name
  ami           = var.instance_ami
  instance_type = var.slave_instance_type
  subnet_id     = var.subnet_id
  user_data = <<-EOF
                #!/bin/bash
                echo "${var.ssh_public_key}" >> /home/ubuntu/.ssh/authorized_keys
                EOF
}
resource "aws_instance" "kubernetes-slave2" {
  tags = {
    Name = "Kubernetes-Slave2-prod"
  }
  key_name      = var.key_name
  ami           = var.instance_ami
  instance_type = var.slave_instance_type
  subnet_id     = var.subnet_id
  user_data = <<-EOF
                #!/bin/bash
                echo "${var.ssh_public_key}" >> /home/ubuntu/.ssh/authorized_keys
                EOF
}
output "slave_private_ip" { 
   description = "Private IP address of the Kubernetes slave instance" 
   value = [aws_instance.kubernetes-slave1.private_ip,aws_instance.kubernetes-slave2.private_ip] 
}
