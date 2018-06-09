import chisel3._
import chisel3.util._
import chisel3.iotesters.Driver
import java.io.PrintWriter

class MemIO extends Bundle(){
    val ben     = Input(UInt(4.W))//字节使能信号，指示读写字节的位置
    val wdata   = Input(UInt(32.W))//写数据
    val wr      = Input(Bool())//写使能信号，为0则表示是读操作
    val addr    = Input(UInt(32.W))//地址，读写均使用同一地址线，字节寻址
    val addr_ok = Output(Bool())//地址、控制信号和写数据已被接收
    val data_ok = Output(Bool())//读数据已经返回或者写数据已经写入
    val rdata   = Output(UInt(32.W))//读数据
}

class MemDemo extends Module(){
    val io = IO(new MemIO())

    val mem = Mem(888888, UInt(8.W))
    //sval reg_raddr = Reg(UInt())

    //val reg_addr_ok    = RegInit(0.U(1.W))
    val reg_data_ok    = RegInit(0.U(1.W))
    val reg_rdata      = RegInit(0.U(32.W))

    val temp = Wire(UInt(32.W))
    val reg_addr_ok    = Wire(UInt(1.W))
    temp := Cat(io.addr(31,2),Fill(2,0.U))

    io.addr_ok  := reg_addr_ok
    io.data_ok  := reg_data_ok
    io.rdata    := reg_rdata

    //addr握手
    when(io.ben != 0.U){
        reg_addr_ok := 1.U
        when(io.wr){
            when(io.ben === "b0001".U
                || io.ben === "b0010".U
                || io.ben === "b0100".U
                || io.ben === "b1000".U){
                //写1个字节
                mem(io.addr      ) := io.wdata( 7, 0)
                reg_rdata := Cat(   Fill(24,0.U),
                                    io.wdata( 7, 0))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b0011".U
                    || io.ben === "b1100".U){
                //写2个字节
                mem(io.addr      ) := io.wdata( 7, 0)
                mem(io.addr + 1.U) := io.wdata(15, 8)
                reg_rdata := Cat(    Fill(16,0.U),
                                    io.wdata(15, 8),
                                    io.wdata( 7, 0))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b1111".U){
                //写4个字节
                mem(io.addr      ) := io.wdata( 7, 0)
                mem(io.addr + 1.U) := io.wdata(15, 8)
                mem(io.addr + 2.U) := io.wdata(23,16)
                mem(io.addr + 3.U) := io.wdata(31,24)
                reg_rdata := Cat(   io.wdata(31,24),
                                    io.wdata(23,16),
                                    io.wdata(15, 8),
                                    io.wdata( 7, 0))
                reg_data_ok := 1.U
            }.otherwise{
                reg_data_ok := 0.U
                reg_rdata    := 0.U
            }
        }.otherwise{
            //reg_raddr := io.addr
            //从mem中读取
            //读取完成，进行数据握手
            when(io.ben === "b0001".U){
                //读1个字节
                reg_rdata := Cat(   Fill(24,0.U),
                                    mem(io.addr))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b0010".U){
                //读1个字节
                reg_rdata := Cat(   Fill(16,0.U),
                                    mem(io.addr),
                                    Fill(8,0.U))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b0100".U){
                //读1个字节
                reg_rdata := Cat(   Fill(8,0.U),
                                    mem(io.addr),
                                    Fill(16,0.U))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b1000".U){
                //读1个字节
                reg_rdata := Cat(   mem(io.addr),
                                    Fill(24,0.U))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b0011".U){
                //读2个字节
                reg_rdata := Cat(   Fill(16,0.U),
                                    mem(io.addr + 1.U),
                                    mem(io.addr      ))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b1100".U){
                //读2个字节
                reg_rdata := Cat(   mem(io.addr + 1.U),
                                    mem(io.addr      ),
                                    Fill(16,0.U))
                reg_data_ok := 1.U
            }.elsewhen(io.ben === "b1111".U){
                //读4个字节
                reg_rdata := Cat(   mem(io.addr + 3.U),
                                    mem(io.addr + 2.U),
                                    mem(io.addr + 1.U),
                                    mem(io.addr      ))
                reg_data_ok := 1.U
            }.otherwise{
                reg_data_ok := 0.U
                reg_rdata    := 0.U
            }
        }
    }.otherwise{
        reg_addr_ok := 0.U
        reg_data_ok := 0.U
        reg_rdata    := 0.U
    }

   // printf("%d %d\n",io.addr_ok,io.data_ok)




}
