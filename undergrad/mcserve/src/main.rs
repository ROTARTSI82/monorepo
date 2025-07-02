mod netdata;

use clap::Parser;
use tokio::{io};
use tokio::io::{AsyncBufReadExt, AsyncReadExt, AsyncWriteExt, BufReader};
use tokio::net::{TcpListener, TcpStream};
use core::net::SocketAddr;
use std::io::Cursor;
use anyhow::{anyhow, ensure, Error};
use crate::netdata::{write_packet, HandshakePacket, InitPacket, MCAsyncRWExt};

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
                        Err(e) => println!("client errored out {:?}", e),
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
        "name": "1.21.7",
        "protocol": 772
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
    "enforcesSecureChat": false
}"##;


async fn client_connected(mut client: ConnectedClient) -> Result<(), Error> {
    println!("new connection: {}", client.addr);
    let mut sock = &mut client.sock;
    sock.set_nodelay(true)?;
    
    let mut vec = Cursor::new(Vec::with_capacity(20));
    vec.write_u8(0).await?;
    
    // handshake
    let handshake = netdata::rpack_init(sock).await?;
    match handshake {
        InitPacket::Intention { addr, protocol, intent, port } => {
            println!("handshake {addr}:{port} with proto {protocol} intent {intent}");
            if intent == 1 { // status
                loop {
                    let ping = netdata::rpack_handshake(sock).await?;
                    match ping {
                        HandshakePacket::StatusRequest {} => {
                            println!("status request got");
                            write_packet!(sock { id: i32 = 0, payload: (LimitedString<4096>) = HARDCODE });
                            println!("sent info");
                        }
                        HandshakePacket::Ping { payload } => {
                            println!("ping payload {payload}");
                            write_packet!(sock { id: i32 = 1, p: i64 = payload});
                            println!("sent pong");
                            break;
                        }
                    }
                }
            } else if intent == 2 {
                
            } else if intent == 3 {
                
            } else {
                Err(anyhow!("invalid handshake intent {intent}"))?
            }
        }
        InitPacket::LegacyServerPing { payload } => {
            ensure!(payload == 1);
            println!("legacy server ping successful");
        }
        _ => Err(anyhow!("invalid handshake"))?
    }
    
    while let x = sock.read_i8().await? {
        print!("{x}\t");
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





