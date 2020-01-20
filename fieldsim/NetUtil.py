import socket


def isServiceAlive(host: str, port: int) -> bool:
    sock: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        return not sock.connect_ex((host, port))
    except socket.gaierror as e:
        return False
