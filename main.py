import socket

if __name__ == '__main__':

    print("Hello")
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #replace ip address of what the ip address of the phone is
    ip = "192.168.0.100"  #socket.gethostbyname("127.0.0.1")
    port = 50000
    address = (ip, port)
    print("Trying to connect to " + ip + ":" + str(port))
    client.connect(address)

    while True:
        data = client.recv(1024)
        print(data)