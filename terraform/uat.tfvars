vpc_cidr_block = "10.2.0.0/16"
subnet_cidr_block = "10.2.1.0/24"
avail_zone = "us-east-2c"
env_prefix = "uat"
my_ip = "52.14.135.171/32"
public_key_location = "/root/.ssh/id_rsa.pub"
instance_type = "t2.medium"
ssh_key_private = "/root/.ssh/id_rsa"

# terraform apply -var-file="uat.tfvars"