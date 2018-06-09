package Pipeline

import chisel3._
import chisel3.util._
import java.io.PrintWriter
import chisel3.iotesters.Driver

class MemTopIO extends Bundle() {
    //→WB
    val dat_to_reg  = Output(UInt(32.W))//写回寄存器文件的数据
    val BadVAddr    = Output(UInt(32.W))//地址错例外的错地址
    val ExcCode     = Output(UInt(5.W))//控制寄存 Cause  ExcCode
    val AddrExc     = Output(Bool())

    //MEM级消化的信号
    val MemEn       = Input(Bool())//1表示有内存取
    val dmem_addr_In= Input(UInt(32.W))//写入或取内存的地址


    //sramlike
    val mem_data_ok      = Input(Bool())
    val mem_addr_ok      = Input(Bool())
    val mem_rdata        = Input(UInt(32.W))

    //val mem_notOK        = Output(Bool())
    val stop_ben        = Output(Bool())

    val mem_type    = Input(UInt(2.W))
    val mem_et      = Input(UInt(1.W))
    val mem_cmd     = Input(UInt(2.W))



}


class MemTop extends Module(){
    val io      = IO(new MemTopIO())
    //val dmem    = Mem(1048576, UInt(8.W))//8位为1字节
    val data    = Wire(UInt(32.W))

    val addrsent = RegInit(0.U(1.W))
    val stop = RegInit(0.U(1.W))

    io.stop_ben := stop

    when(io.mem_data_ok){
        data := io.mem_rdata
        //storen := 0.U
        //gg := io.mem_rdata
    }.otherwise{
        data := 0.U
        //gg := 0.U
    }


    val gg  = Wire(UInt(32.W))

    //printf("%d",gg)
/**
    when(io.mem_addr_ok){

    }.otherwise{
        addrsent := 1.U
    }

    when(io.mem_data_ok){
        addrsent := 0.U
    }.otherwise{}


    //when((addrsent === 1.U || !io.mem_addr_ok) && stop === 0.U ){
    when(addrsent === 1.U){
        io.mem_notOK := 1.U
        //gg := 22.U
    }.otherwise{
        io.mem_notOK := 0.U
        //gg := 33.U
    }*/

   /** when(addrsent === 1.U && io.mem_addr_ok){
        //stop    := 1.U
    }.otherwise{
        //stop    := 0.U
    }*/
/**
    when(stop === 1.U && !io.mem_addr_ok){
        stop := 1.U
    }*/


    when(io.MemEn){

        when(io.mem_type === 2.U){//MT_W SW LW
            /** 存取字（32位）
             31       24 23       16 15        8 7         0
             ___________ ___________ ___________ ___________
            |     3     |     2     |     1     |     0     |
            */
            when(io.dmem_addr_In(1,0)===0.U){
                when(io.mem_cmd === 1.U){//SW
                    //storen := 1.U
                    io.dat_to_reg  := 0.U
                    //gg := 1.U
                    /**when(io.mem_addr_ok && !io.mem_data_ok){
                        io.mem_notOK := 1.U
                    }.otherwise{
                        io.mem_notOK := 0.U
                    }*/
                    //io.mem_notOK := 0.U
                    gg:=222222.U
                    io.dat_to_reg := 0.U

                }.elsewhen(io.mem_cmd === 0.U){//LW
                    io.dat_to_reg  := data
                    /**when(!io.mem_addr_ok ||storen === 1.U){
                        io.mem_notOK := 1.U
                        //gg:=222.U
                    }.otherwise{
                        io.mem_notOK := 0.U
                    }*/
                }.otherwise{}
            }
        }
        .elsewhen(io.mem_type === 0.U){//MT_B LB LBU SB
            /** 存取字节8位）
             7         0
             ___________
            |     0     |
            */
            when(io.mem_cmd ===1.U){//wr dm
                io.dat_to_reg := 0.U
                //gg := 2.U
                when(io.mem_et===0.U){//SB
                    //storen := 1.U
                    //io.mem_notOK := 0.U
                }.otherwise{}
            }.elsewhen(io.mem_cmd ===0.U){//LBU&LB
                when(io.dmem_addr_In(1,0) === "b00".U){
                    when(io.mem_et===0.U){//LBU
                        io.dat_to_reg           := Cat(Fill(24,0.U),data(7,0))
                    }.elsewhen(io.mem_et===1.U){//LB
                        io.dat_to_reg           := Cat(Fill(24,data(7)),data(7,0))
                    }
                }.elsewhen(io.dmem_addr_In(1,0) === "b01".U){
                    when(io.mem_et===0.U){//LBU
                        io.dat_to_reg           := Cat(Fill(24,0.U),data(15,8))
                    }.elsewhen(io.mem_et===1.U){//LB
                        io.dat_to_reg           := Cat(Fill(24,data(15)),data(15,8))
                    }
                }.elsewhen(io.dmem_addr_In(1,0) === "b10".U){
                    when(io.mem_et===0.U){//LBU
                        io.dat_to_reg           := Cat(Fill(24,0.U),data(23,16))
                    }.elsewhen(io.mem_et===1.U){//LB
                        io.dat_to_reg           := Cat(Fill(24,data(23)),data(23,16))
                    }
                }.elsewhen(io.dmem_addr_In(1,0) === "b11".U){
                    when(io.mem_et===0.U){//LBU
                        io.dat_to_reg           := Cat(Fill(24,0.U),data(31,24))
                    }.elsewhen(io.mem_et===1.U){//LB
                        io.dat_to_reg           := Cat(Fill(24,data(31)),data(31,24))
                    }
                }
                /**when(!io.mem_addr_ok  || storen === 1.U){
                    io.mem_notOK := 1.U
                }.otherwise{
                    io.mem_notOK := 0.U
                }*/
            }
        }
        .elsewhen(io.mem_type === 1.U){//MT_H  LH LHU SH
            /** 存取半字16位）
             15        8 7         0
             ___________ ___________
            |     1     |     0     |
            */
            when(io.mem_cmd === 1.U){
                when(io.mem_et=== 0.U){//SH
                    when(io.dmem_addr_In(0) === 0.U){
                        io.dat_to_reg := 0.U
                        //storen := 1.U
                    }
                    //io.mem_notOK := 0.U
                    //gg:=4444444.U
                }.otherwise{}
            }.elsewhen(io.mem_cmd === 0.U){//LHU&LH
                /**when(!io.mem_addr_ok || storen === 1.U){
                    io.mem_notOK := 1.U
                    gg:=444.U
                }.otherwise{
                    io.mem_notOK := 0.U
                }*/
                when(io.mem_et === 0.U){//LHU
                    when(io.dmem_addr_In(0) === 0.U){
                        when(io.dmem_addr_In(1,0) === "b00".U){
                            io.dat_to_reg := Cat(Fill(16,0.U), data(15,0))
                        }.elsewhen(io.dmem_addr_In(1,0) === "b10".U){
                            io.dat_to_reg  := Cat(Fill(16,0.U), data(31,16))
                        }
                    }
                }.elsewhen(io.mem_et === 1.U){//LH
                    when(io.dmem_addr_In(0) === 0.U){
                        when(io.dmem_addr_In(1,0) === "b00".U){
                            io.dat_to_reg  := Cat(Fill(16,data(15)), data(15,0))
                        }.elsewhen(io.dmem_addr_In(1,0) === "b10".U){
                            io.dat_to_reg  := Cat(Fill(16,data(31)), data(31,16))
                        }
                    }
                }.otherwise{}
            }.otherwise{}
        }.otherwise{}
    }.otherwise{
        //无存
    }

    //printf("%d",gg)
}
