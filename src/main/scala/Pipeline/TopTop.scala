package Pipeline

import chisel3._
import chisel3.util._
import chisel3.iotesters.Driver
import java.io.PrintWriter

class ExternalIO extends Bundle(){
    val test_wr     = Input(Bool())//1预先载入指令,0cpu工作
    val test_addr   = Input(UInt(32.W))
    val test_inst   = Input(UInt(32.W))

    val MEM_WB_inst  = Output(UInt(32.W))
}


class TopTop extends Module(){
    val io      = IO(new ExternalIO())
    val imem    = Module(new MemDemo())
    val dmem    = Module(new MemDemo())
    val cpu     = Module(new Top())

    io.MEM_WB_inst := cpu.io.MEM_WB_inst

    when(io.test_wr){
        cpu.io.reset        := 0.U//0复位

        imem.io.ben         := "b1111".U
        imem.io.wdata       := io.test_inst
        imem.io.wr          := 1.U
        imem.io.addr        := io.test_addr
    }.otherwise{
        cpu.io.reset        := 1.U

        imem.io.ben         := cpu.io.if_ben
        imem.io.wdata       := cpu.io.if_wdata
        imem.io.wr          := cpu.io.if_wr
        imem.io.addr        := cpu.io.if_addr
        cpu.io.if_addr_ok   := imem.io.addr_ok
        cpu.io.if_data_ok   := imem.io.data_ok
        cpu.io.if_rdata     := imem.io.rdata

        dmem.io.ben         := cpu.io.mem_ben
        dmem.io.wdata       := cpu.io.mem_wdata
        dmem.io.wr          := cpu.io.mem_wr
        dmem.io.addr        := cpu.io.mem_addr
        cpu.io.mem_addr_ok   := dmem.io.addr_ok
        cpu.io.mem_data_ok   := dmem.io.data_ok
        cpu.io.mem_rdata     := dmem.io.rdata
    }
}
