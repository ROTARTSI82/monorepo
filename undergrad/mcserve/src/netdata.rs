use anyhow::{anyhow, ensure, Error};
use tokio::io::{AsyncRead, AsyncReadExt};
use tokio::net::TcpStream;
use tokio::pin;

const SEGMENT_BITS: i32 = 0x7F;
const CONTINUE_BIT: u8 = 0x80;


// macro hack to get the varying string lengths to work
type String256 = String;

// rust macros fucking suck
// what is this bullshit i spent hours figuring out how to get it to parse correctly
macro_rules! packets {
    (def_field $ta:ident $stream:ident i32 $f:ident) => {
        let ($f, add) = $stream.read_var_int().await?; $ta += add;
    };
    (def_field $ta:ident $stream:ident String256 $f:ident) => { 
        let ($f, add) = $stream.read_string(256).await?; $ta += add;
    };
    
    (def_field $ta:ident $stream:ident u16 $f:ident) => { 
        let $f = $stream.read_u16().await?; $ta += 2;
    };
    (def_field $ta:ident $stream:ident u8 $f:ident) => { 
        let $f = $stream.read_u8().await?; $ta += 1;
    };

    (coerce $b:path, $other:tt) => {{ $b $other }};
    (resolve_arm $accum:ident $len:ident $stream:ident $name:path { $($field:ident: $ty:tt),* }) => {{
        $( packets!(def_field $accum $stream $ty $field); )*
        ensure!($accum == $len);
        packets!(coerce $name, { $($field, )* })
    }};

    ($($num:literal: $name:ident $body:tt),+) => {
        #[derive(Debug)]
        pub enum ClientPacket {
            $(
                $name $body,
            )*
        }

        pub async fn read_packet(mut stream: &mut TcpStream) -> Result<ClientPacket, Error> {
            let (length, _) = stream.read_var_int().await?;
            let (packet_id, mut accum) = stream.read_var_int().await?;
            println!("recv packet {length} of id {packet_id}");

            Ok(match packet_id {
                $(
                $num => {
                    packets!(resolve_arm accum length stream ClientPacket::$name $body)
                },
                )*
                _ => Err(anyhow!("invalid packet id {}", packet_id))?,
            })
        }
    }
}

packets!(
    0: Intention { protocol: i32, addr: String256, port: u16, intent: i32 },
    0xFE: LegacyServerPing { payload: u8 }
);

trait ReadExt: AsyncRead {
    async fn read_var_int(&mut self) -> Result<(i32, i32), Error> where Self: Unpin {
        let mut ret = 0i32;
        let mut pos = 0i32;

        Ok(loop {
            let next = self.read_u8().await?;
            ret |= (next as i32 & SEGMENT_BITS) << (7*pos);
            pos += 1;
            if (next & CONTINUE_BIT) == 0 {
                break (ret, pos);
            }
            
            if (7*pos) >= 32 {
                Err(anyhow!("VarInt too large"))?
            }
        })
    }

    async fn read_string(&mut self, max_len: i32) -> Result<(String, i32), Error> where Self: Unpin {
        let (length, read) = self.read_var_int().await?;
        ensure!(length <= max_len*3 + 3 && length >= 1);
        let mut buf = vec![0u8; length as usize];
        self.read_exact(&mut buf).await?;
        Ok((String::from_utf8(buf)?, length + read))
    }
}

impl ReadExt for &mut TcpStream {}
