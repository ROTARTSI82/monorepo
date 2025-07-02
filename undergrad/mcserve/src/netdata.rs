use std::io;
use std::io::Cursor;
use std::ops::{Add, AddAssign, BitAnd, BitOrAssign, Mul, Not, Shl, ShrAssign};
use anyhow::{anyhow, ensure, Error};
use tokio::io::{AsyncRead, AsyncReadExt, AsyncWriteExt, AsyncWrite};
use tokio::net::TcpStream;

const SEGMENT_BITS: i64 = 0x7F;
const CONTINUE_BIT: u8 = 0b10000000;


// macro hack to get the varying string lengths to work
pub type LimitedString<const L: i32> = String;
pub type VarInt = i32;
pub type VarLong = i64;

// rust macros fucking suck
// what is this bullshit i spent hours figuring out how to get it to parse correctly
macro_rules! packets {
    (def_field $ta:ident $stream:ident VarInt $f:ident) => {
        let ($f, add) = $stream.read_var_int().await?; $ta += add;
    };
    (def_field $ta:ident $stream:ident VarLong $f:ident) => {
        let ($f, add) = $stream.read_var_long().await?; $ta += add;
    };
    (def_field $ta:ident $stream:ident (LimitedString<$l:literal>) $f:ident) => {
        let ($f, add) = $stream.read_string($l).await?; $ta += add;
    };
    (def_field $ta:ident $stream:ident u16 $f:ident) => {
        let $f = $stream.read_u16().await?; $ta += 2;
    };
    (def_field $ta:ident $stream:ident u8 $f:ident) => {
        let $f = $stream.read_u8().await?; $ta += 1;
    };
    (def_field $ta:ident $stream:ident i64 $f:ident) => {
        let $f = $stream.read_i64().await?; $ta += 8;
    };

    (coerce $b:path, $other:tt) => {{ $b $other }};
    (resolve_arm $accum:ident $len:ident $stream:ident $name:path { $($field:ident: $ty:tt),* }) => {{
        $( packets!(def_field $accum $stream $ty $field); )*
        dbg!($accum == $len);
        packets!(coerce $name, { $($field, )* })
    }};

    ($enum_name:ident, $func_name:ident; $($num:literal: $name:ident $body:tt),+) => {
        #[derive(Debug)]
        pub enum $enum_name {
            $(
                $name $body,
            )*
        }

        pub async fn $func_name(mut stream: &mut TcpStream) -> Result<$enum_name, Error> {
            let (length, _) = stream.read_var_int().await?;
            let (packet_id, mut accum) = stream.read_var_int().await?;
            println!("\t {} recv {}", stringify!($enum_name), packet_id);

            Ok(match packet_id {
                $(
                $num => {
                    packets!(resolve_arm accum length stream $enum_name::$name $body)
                },
                )*
                _ => Err(anyhow!("invalid packet id {} in {}", packet_id, stringify!($enum_name)))?,
            })
        }
    }
}

packets!(InitPacket, rpack_init;
    0: Intention { protocol: VarInt, addr: (LimitedString<256>), port: u16, intent: VarInt },
    0xFE: LegacyServerPing { payload: u8 }
);

packets!(StatusPacket, rpack_status;
    0: StatusRequest { },
    1: Ping { payload: i64 }
);

macro_rules! write_packet {
    (accum (LimitedString<$l:literal>) $acc:ident; $f:ident) => {
        let len = $f.as_bytes().len();
        ensure!(len <= 3*$l + 3 && 1 <= len);
        $acc += <&mut tokio::net::TcpStream as MCAsyncRWExt>::len_var_int(len as i32);
        $acc += len as i32;
    };
    (accum VarInt $acc:ident; $f:ident) => {
        $acc += <&mut tokio::net::TcpStream as MCAsyncRWExt>::len_var_int($f); };
    (accum VarLong $acc:ident; $f:ident) => {
        $acc += <&mut tokio::net::TcpStream as MCAsyncRWExt>::len_var_long($f); };
    (accum i64 $acc:ident; $f:ident) => { $acc += 8; };

    (do_write (LimitedString<$l:literal>) $stream:ident; $elem:ident) => {
        $stream.write_string($elem).await?;
    };
    (do_write VarInt $stream:ident; $elem:ident) => { $stream.write_var_int($elem).await?; };
    (do_write VarLong $stream:ident; $elem:ident) => { $stream.write_var_long($elem).await?; };
    (do_write i64 $stream:ident; $elem:ident ) => { $stream.write_i64($elem).await?; };
    ($stream:ident { $($elem:ident: $ty:tt = $expr:expr),+ }) => {
        let mut priv_accum = 0;
        $(
            let $elem = $expr;
            write_packet!(accum $ty priv_accum; $elem);
        )*

        let mut size: usize = priv_accum.try_into()?;
        println!("\tcalculated len {priv_accum}");
        let mut buf = Cursor::new(Vec::with_capacity(size + 4));
        buf.write_var_int(priv_accum).await?;
        size += buf.get_ref().len(); // this part is not included in the length calculation

        $(
            write_packet!(do_write $ty buf; $elem);
        )*
        ensure!(buf.get_ref().len() == size);
        $stream.write_all(buf.get_ref()).await?;
    }
}

pub(crate) use write_packet;

macro_rules! impl_rw {
    (resolve_len i32) => {{ 32 }};
    (resolve_len i64) => {{ 64 }};
    (to_unsigned i32) => { u32 };
    (to_unsigned i64) => { u64 };
    ($ty:tt: $rname:ident, $wname:ident, $qname:ident) => {
        async fn $rname (&mut self) -> Result<($ty, i32), Error> where Self: Unpin {
            let mut ret = 0;
            let mut pos = 0;
            let mut buf = [0; 4];

            Ok(loop {
                let next = self.eof_read_u8().await?;
                ret |= (next as $ty & SEGMENT_BITS as $ty) << (7 * pos);
                pos += 1;
                if (next & CONTINUE_BIT) == 0 {
                    break (ret, pos);
                }

                if 7 * pos >= impl_rw!(resolve_len $ty) {
                    Err(anyhow!("VarInt too large"))?
                }
            })
        }

        async fn $wname (&mut self, val: $ty) -> Result<(), Error>
        where Self: Unpin {
            // we need logical shift not arithmetic
            type Unsigned = impl_rw!(to_unsigned $ty);
            let mut val = val as Unsigned;
            loop {
                if val & !(SEGMENT_BITS as Unsigned) == 0 {
                    self.write_u8(val as u8).await?;
                    break Ok(());
                }

                self.write_u8((val & SEGMENT_BITS as Unsigned) as u8 | CONTINUE_BIT).await?;
                val = val >> 7;
            }
        }

        fn $qname (val: $ty) -> i32 {
            type Unsigned = impl_rw!(to_unsigned $ty);
            let mut val = val as Unsigned;
            let mut cnt = 0;
            loop {
                if val & !(SEGMENT_BITS as Unsigned) == 0 {
                    break cnt + 1;
                }

                val = val >> 7;
                cnt += 1;
            }
        }
    }
}


pub trait MCAsyncRWExt: AsyncRead + AsyncWrite {
    impl_rw!(i32: read_var_int, write_var_int, len_var_int);
    impl_rw!(i64: read_var_long, write_var_long, len_var_long);

    async fn read_string(&mut self, max_len: i32) -> Result<(String, i32), Error> where Self: Unpin {
        let (length, read) = self.read_var_int().await?;
        ensure!(length <= max_len*3 + 3 && length >= 1);
        let mut buf = vec![0u8; length as usize];
        self.read_exact(&mut buf).await?;
        Ok((String::from_utf8(buf)?, length + read))
    }

    async fn write_string(&mut self, val: &str) -> Result<(), Error> where Self: Unpin {
        self.write_var_int(val.as_bytes().len() as i32).await?;
        self.write_all(val.as_bytes()).await?;
        Ok(())
    }

    async fn eof_read_u8(&mut self) -> Result<u8, Error> where Self: Unpin {
        let mut x = [0; 1];
        let read = self.read(&mut x).await?;
        if read == 1 {
            Ok(x[0])
        } else {
            println!("connection closed with bytes={read}");
            Err(anyhow!("connection closed by other side"))?
        }
    }
}

impl MCAsyncRWExt for &mut TcpStream {}
impl MCAsyncRWExt for Cursor<Vec<u8>> {}
