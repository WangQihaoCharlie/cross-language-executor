# cross-language-executor
This project provides an elegant solution for executing Python code within a Java Spring context. It leverages Jupyter Notebook and Redis Stream for seamless integration.

## How to use
Start by pulling the docker image provided by the Jupyter Notebook team:

```bash
git clone https://github.com/jupyter/kernel_gateway_demos
cd kernel_gateway_demos/python_client_example
docker-compose build
docker-compose up
```

Set up Redis for data transfer between your Java system and the code running in Jupyter. Pull a Redis docker image and configure the application.properties file to match your desired port and host.

```bash
docker pull redis
```

Additionally, you'll need a Minio server. Pull the respective image and upload your code. It will be automatically downloaded and executed based on the parameters defined in your interfaces.

```bash
docker pull minio
```

Note: We utilize gRPC for file downloading. Make sure to run the protobuf plugins (Custom & compile), and refer to the gRPC Java documentation (https://github.com/grpc/grpc-java) for more details.

Adjust the application.properties file according to your setup, e.g., modifying passwords or usernames.
While this system seamlessly integrates with RestFul APIs, you can also employ RabbitMQ as an RPC client in a microservices setting. Here's an example client:

```java
    public String sendRpcRequest(String message) throws Exception {

        String responseQueueName = "rpc.response.queue." + UUID.randomUUID(); 

        log.info("Response received from {}", responseQueueName);
        MessageProperties properties = new MessageProperties();
        properties.setReplyTo(responseQueueName);

        Message requestMessage = new Message(message.getBytes(), properties);

        Message response = rabbitTemplate.sendAndReceive("sender", requestMessage);

        return new String(response.getBody());
    }

```
Please setup RabbitMQ according to your situation.

## Test it
There are two RestFul apis
1. [Your host]/compiler/execute-file
2. [Your host]/compiler/execute-single-line

1 takes in 3 request params, which is instanceId, type, data
instanceId is the python file name you upload in your minIO server and type is the bucket Name, data is the value you want to transfer into your python code

For 2, just type in a python code such as "print("Hello World!")", the result will be displayed. You can't transfer data into jupyter in this case.


For the python code, you will need to write a redis client to receive the data from the redis stream, here is an example

``` Python
from IPython.core.getipython import get_ipython
import redis


kernel = get_ipython().kernel
cf=kernel.config['IPKernelApp']['connection_file']
kernel_id = cf.split("\\")[-1].split("kernel-")[1].split(".")[0]

redis_host = '192.168.1.47'
redis_port = 6379
redis_client = redis.StrictRedis(host=redis_host, port=redis_port, decode_responses=True)



def read_messages_from_stream(stream_key, peek):
    messages = redis_client.xread({stream_key: '0'}, count=1, block=1000)
    while("-" not in list(messages[0][1][0][1].keys())[0]):
        redis_client.xdel(stream_key, messages[0][1][0][0])
        messages = redis_client.xread({stream_key: '0'}, count=1, block=1000)
    
    if(not peek):
        redis_client.xdel(stream_key, messages[0][1][0][0])


    return messages



if __name__ == '__main__':

    messages = read_messages_from_stream(kernel_id, False)
    print(messages)


```

Thanks you for reading! Send me an email[wan2901@dcds.edu] or a pull request if you have any problem with using this project, I am happy to help.

