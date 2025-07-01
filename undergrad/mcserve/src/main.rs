mod netdata;

use clap::Parser;
use tokio::{io};
use tokio::io::{AsyncBufReadExt, BufReader};
use tokio::net::{TcpListener, TcpStream};
use core::net::SocketAddr;
use anyhow::{anyhow, ensure, Error};
use crate::netdata::ClientPacket;

#[derive(Parser, Debug)]
#[command(version, about)]
struct Args {
    #[arg(short, long, default_value_t=String::from("0.0.0.0"))]
    addr: String,

    #[arg(short, long, default_value_t=25565)]
    port: u16,
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    Ok(tokio::runtime::Builder::new_multi_thread()
        .enable_all()
        .build()?
        .block_on(async_main())?)
}


async fn async_main() -> Result<(), Box<dyn std::error::Error>> {
    let args = Args::parse();
    let server = TcpListener::bind((args.addr.as_str(), args.port)).await?;
    let server = tokio::spawn(server_listener(Server::new(server)));

    println!("Up on {}:{} awaiting connections", args.addr, args.port);

    let mut lines = BufReader::new(io::stdin()).lines();
    while let Some(line) = lines.next_line().await? {
        if line.starts_with("exit") {
            break;
        }
    }

    server.abort();
    Ok(())
}

struct Server {
    listener: TcpListener,
}

async fn server_listener(serv: Server) -> ! {
    loop {
        match serv.listener.accept().await {
            Ok((socket, addr)) => {
                tokio::spawn(async move {
                    match client_connected(ConnectedClient::new(socket, addr)).await {
                        Err(e) => println!("client errored out: {e}"),
                        Ok(()) => {},
                    }
                });
            }
            Err(e) => println!("Connection error: {}", e),
        }
    }
}


impl Server {
    fn new(listener: TcpListener) -> Self {
        Self { listener }
    }
}

impl Drop for Server {
    fn drop(&mut self) {
        println!("Stopping server");
    }
}


struct ConnectedClient {
    sock: TcpStream,
    addr: SocketAddr
}

const HARDCODE: &str = r##"{
    "version": {
        "name": "1.21.5",
        "protocol": 770
    },
    "players": {
        "max": 100,
        "online": 5,
        "sample": [
            {
                "name": "thinkofdeath",
                "id": "4566e69f-c907-48ee-8d71-d7ba5aa00d20"
            }
        ]
    },
    "description": {
        "text": "Hello, world!"
    },
    "favicon": "data:image/png;base64,<data>",
    "enforcesSecureChat": false
}"##;


async fn client_connected(mut client: ConnectedClient) -> Result<(), Error> {
    println!("new connection: {}", client.addr);
    let sock = &mut client.sock;
    
    // handshake
    let handshake = netdata::read_packet(sock).await?;
    match handshake {
        ClientPacket::Intention { addr, protocol, intent, port } => {
            println!("handshake {addr}:{port} with proto {protocol} intent {intent}");
            if intent == 1 { // status
                
            } else if intent == 2 {
                
            } else if intent == 3 {
                
            } else {
                Err(anyhow!("invalid handshake intent {intent}"))?
            }
        }
        ClientPacket::LegacyServerPing { payload } => {
            ensure!(payload == 1);
        }
        _ => Err(anyhow!("invalid handshake"))?
    }

    Ok(())
}

impl ConnectedClient {
    fn new(sock: TcpStream, addr: SocketAddr) -> Self {
        Self { sock, addr }
    }
}

impl Drop for ConnectedClient {
    fn drop(&mut self) {
        println!("disconnecting {:?}", self.addr)
    }
}





