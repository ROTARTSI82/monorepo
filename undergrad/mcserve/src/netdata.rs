use anyhow::{anyhow, ensure, Error};
use tokio::io::{AsyncRead, AsyncReadExt, AsyncWriteExt, AsyncWrite};
use tokio::net::TcpStream;

const SEGMENT_BITS: i64 = 0x7F;
const CONTINUE_BIT: u8 = 0b10000000;


// macro hack to get the varying string lengths to work
#[derive(Debug)]
pub struct LimitedString<const L: i32>(pub String);
#[derive(Debug)]
pub struct VarInt(pub i32);
#[derive(Debug)]
pub struct VarLong(pub i64);
#[derive(Debug)]
pub struct Uuid(pub u128);

pub type Identifier = LimitedString<32767>;

// rust macros fucking suck
// what is this bullshit i spent hours figuring out how to get it to parse correctly
macro_rules! packets {
    (coerce $b:path, $other:tt) => {{ $b $other }};
    (read_from $ty:tt, $stream:ident) => {{ <$ty>::read_from($stream).await? }};
    
    (resolve_arm $accum:ident $len:ident $stream:ident $name:path { $($field:ident: $ty:path),* }) => {{
        $(
            #[allow(unused_parens)]
            let ($field, add) = packets!(read_from ($ty), $stream); $accum += add;
        )*
        ensure!($accum == $len);
        packets!(coerce $name, { $($field, )* })
    }};

    ($enum_name:ident, $func_name:ident; $($num:literal: $name:ident $body:tt),+) => {
        #[allow(unused_parens)]
        #[derive(Debug)]
        pub enum $enum_name {
            $(
                $name $body,
            )*
        }

        pub async fn $func_name(stream: &mut TcpStream) -> Result<$enum_name, Error> {
            let (length, _) = VarInt::read_from(stream).await?;
            let length = length.0;
            let (packet_id, mut accum) = VarInt::read_from(stream).await?;
            let packet_id = packet_id.0;
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
    0x00: Intention { protocol: VarInt, addr: LimitedString::<256>, port: u16, intent: VarInt },
    0xFE: LegacyServerPing { payload: u8 }
);

packets!(StatusPacket, rpack_status;
    0x00: StatusRequest { },
    0x01: Ping { payload: i64 }
);

packets!(LoginPacket, rpack_login;
    0x00: LoginStart { name: LimitedString::<16>, uuid: u128 },
    0x01: EncryptionResponse { secret: Vec::<u8>, verify: Vec::<u8> },
    // 0x02 requires a custom type todo
    0x03: LoginAck { },
    0x04: CookieResponse { id: Identifier, payload: Option<Vec<u8>> }
);

macro_rules! write_packet {
    ($stream:ident { $($elem:ident = $expr:expr),+ }) => {
        let mut priv_accum = 0;
        $(
            let $elem = $expr;
            priv_accum += $elem.get_len();
        )*

        let mut size: usize = priv_accum.try_into()?;
        println!("\tcalculated len {priv_accum}");
        let mut buf = Cursor::new(Vec::with_capacity(size + 4));
        VarInt(priv_accum).write_to(&mut buf).await?;
        size += buf.get_ref().len(); // this part is not included in the length calculation

        $(
            $elem.write_to(&mut buf).await?;
        )*
        ensure!(buf.get_ref().len() == size);
        $stream.write_all(buf.get_ref()).await?;
    }
}

pub(crate) use write_packet;

macro_rules! impl_rw {
    (resolve_len VarInt) => {{ 32 }};
    (resolve_len VarLong) => {{ 64 }};
    (to_unsigned VarInt) => { u32 };
    (to_unsigned VarLong) => { u64 };
    (unwrap VarInt) => { i32 };
    (unwrap VarLong) => { i64 };
    
    ($ty:tt) => {
        
        impl MCRWObj for $ty { 
            async fn read_from<T>(mut stream: &mut T) -> Result < ( $ty, i32), Error>
            where for<'a> &'a mut T: AsyncRead + Unpin, T: Unpin { 
                let mut ret = 0;
                let mut pos = 0;

                type Unwrap = impl_rw!(unwrap $ty);
                Ok( loop {
                    let next = stream.read_u8().await?; // eof_read_u8
                    ret |= (next as Unwrap & SEGMENT_BITS as Unwrap) << (7 * pos);
                    pos += 1;
                    if (next & CONTINUE_BIT) == 0 {
                        break ($ty(ret), pos);
                    }

                    if 7 * pos >= impl_rw!(resolve_len $ty) {
                        Err(anyhow!("VarInt too large"))?
                    }
                })
            }

            async fn write_to<T>(&self, mut stream: &mut T) -> Result < (), Error >
            where for<'a> &'a mut T: AsyncWrite + Unpin, T: Unpin  {
                // we need logical shift not arithmetic
                type Unsigned = impl_rw!(to_unsigned $ty);
                let mut val = self.0 as Unsigned;
                loop {
                    if val & !(SEGMENT_BITS as Unsigned) == 0 {
                        stream.write_u8(val as u8).await?;
                        break Ok(());
                    }

                    stream.write_u8((val & SEGMENT_BITS as Unsigned) as u8 | CONTINUE_BIT).await?;
                    val >>= 7;
                }
            }
            
            fn get_len(&self) -> i32 {
                type Unsigned = impl_rw!(to_unsigned $ty);
                let mut val = self.0 as Unsigned;
                let mut cnt = 0;
                loop {
                    if val & !(SEGMENT_BITS as Unsigned) == 0 {
                        break cnt + 1;
                    }
    
                    val >>= 7;
                    cnt += 1;
                }
            }
        }
    }
}


pub trait MCRWObj {
    async fn write_to<T>(&self, stream: &mut T) -> Result<(), Error> 
    where for<'a> &'a mut T: AsyncWrite + Unpin, T: Unpin;
    
    async fn read_from<T>(stream: &mut T) -> Result<(Self, i32), Error> 
    where for<'a> &'a mut T: AsyncRead + Unpin, T: Unpin, Self: Sized;
    
    fn get_len(&self) -> i32;
}

impl_rw!(VarInt);
impl_rw!(VarLong);

impl<const L: i32> MCRWObj for LimitedString<L> {
    async fn write_to<T>(&self, mut stream: &mut T) -> Result<(), Error>
    where
            for<'a> &'a mut T: AsyncWrite + Unpin,
            T: Unpin
    {
        VarInt(self.0.len() as i32).write_to(stream).await?;
        stream.write_all(self.0.as_bytes()).await?;
        Ok(())
    }

    async fn read_from<T>(mut stream: &mut T) -> Result<(Self, i32), Error>
    where
            for<'a> &'a mut T: AsyncRead + Unpin,
            T: Unpin,
            Self: Sized
    {
        let (length, read) = VarInt::read_from(stream).await?;
        let length = length.0;
        ensure!((1..=3*L+3).contains(&length));
        let mut buf = vec![0u8; length as usize];
        stream.read_exact(&mut buf).await?;
        Ok((LimitedString::<L>(String::from_utf8(buf)?), length + read))
    }
    
    fn get_len(&self) -> i32 {
        self.0.len() as i32 + VarInt(self.0.len() as i32).get_len()
    }
}


macro_rules! impl_rw_primitive {
    (r u8 $stream:ident) => {{ $stream.read_u8().await? }};
    (w $self:ident u8 $stream:ident) => { $stream.write_u8(*$self).await?; };
    (r u16 $stream:ident) => {{ $stream.read_u16().await? }};
    (w $self:ident u16 $stream:ident) => { $stream.write_u16(*$self).await?; };
    (r i64 $stream:ident) => {{ $stream.read_i64().await? }};
    (w $self:ident i64 $stream:ident) => { $stream.write_i64(*$self).await?; };
    (r u128 $stream:ident) => {{ $stream.read_u128().await? }};
    (w $self:ident u128 $stream:ident) => { $stream.write_u128(*$self).await?; };
    (r i32 $stream:ident) => {{ $stream.read_i32().await? }};
    (w $self:ident i32 $stream:ident) => { $stream.write_i32(*$self).await?; };
    
    ($ty:tt) => {
        impl MCRWObj for $ty {
            async fn write_to<T>(&self, mut stream: &mut T) -> Result<(), Error>
            where
                    for<'a> &'a mut T: AsyncWrite + Unpin,
                    T: Unpin
            {
                impl_rw_primitive!(w self $ty stream);
                Ok(())
            }
        
            async fn read_from<T>(mut stream: &mut T) -> Result<(Self, i32), Error>
            where
                    for<'a> &'a mut T: AsyncRead + Unpin,
                    T: Unpin,
                    Self: Sized
            {
                Ok((impl_rw_primitive!(r $ty stream), size_of::<Self>() as i32))
            }
            
            fn get_len(&self) -> i32 {
                size_of::<Self>() as i32
            }
        }
    }
}

impl_rw_primitive!(u8);
impl_rw_primitive!(u16);
impl_rw_primitive!(i64);
impl_rw_primitive!(i32);
impl_rw_primitive!(u128);

impl<A: MCRWObj> MCRWObj for Vec<A> {
    async fn write_to<T>(&self, stream: &mut T) -> Result<(), Error>
    where
            for<'a> &'a mut T: AsyncWrite + Unpin,
            T: Unpin
    {
        VarInt(self.len() as i32).write_to(stream).await?;
        for elem in self {
            elem.write_to(stream).await?;
        }
        
        Ok(())
    }

    async fn read_from<T>(stream: &mut T) -> Result<(Self, i32), Error>
    where
            for<'a> &'a mut T: AsyncRead + Unpin,
            T: Unpin,
            Self: Sized
    {
        let (len, mut accum) = VarInt::read_from(stream).await?;
        let mut ret = Vec::with_capacity(len.0 as usize);
        for _ in 0..len.0 {
            let (recv, add) = A::read_from(stream).await?;
            accum += add;
            ret.push(recv);
        }
        
        Ok((ret, accum))
    }

    fn get_len(&self) -> i32 {
        (size_of::<A>() * self.len()) as i32 + VarInt(self.len() as i32).get_len() 
    }
}

impl<A: MCRWObj> MCRWObj for Option<A> {
    async fn write_to<T>(&self, mut stream: &mut T) -> Result<(), Error>
    where
            for<'a> &'a mut T: AsyncWrite + Unpin,
            T: Unpin
    {
        match self {
            Some(a) => {
                stream.write_u8(1).await?;
                a.write_to(stream).await?;
            },
            None => {
                stream.write_u8(0).await?;
            }
        }
        
        Ok(())
    }

    async fn read_from<T>(mut stream: &mut T) -> Result<(Self, i32), Error>
    where
            for<'a> &'a mut T: AsyncRead + Unpin,
            T: Unpin,
            Self: Sized
    {
        let present = stream.read_u8().await?;
        if present == 0 {
            Ok((None, 1))
        } else {
            let (ret, recv) = A::read_from(stream).await?;
            Ok((Some(ret), recv + 1))
        }
    }

    fn get_len(&self) -> i32 {
        match self {
            Some(a) => a.get_len() + 1,
            None => 1,
        }
    }
}
