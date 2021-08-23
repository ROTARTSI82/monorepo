
    push64 5u64
    call 8u16 entry->factorial64
entry:
    EXT_DBG_outp u64
    panic


factorial64:
    dref_lbp u64 0u32
    push64 0u64
    eq u64
    conv u64 u8
    jmpif next->ret1
next:
    dref_lbp u64 0u32
    push64 1u64
    sub u64
    call 8u16 recursepoint->factorial64
recursepoint:
    dref_lbp u64 0u32
    mul u64
    ret
ret1:
    push64 1u64
    ret
