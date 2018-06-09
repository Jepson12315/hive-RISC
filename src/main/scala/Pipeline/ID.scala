package Pipeline

import chisel3._
import chisel3.util._
import Instructions._
import ScalarConstants._

object Br
{


    def isSub(fn: UInt) = fn(4)
}
import Br._

class IDEXOUT extends Bundle(){
    val pcRegValid      = Output(Bool())
    val ival            = Output(Bool())//Y：该指令为合法指令；N：不合法
    val fp_val          = Output(Bool())//Y：该指令为合法浮点指令；N：非合法浮点 指令
    val br              = Output(Bool())//Y：分支有效；N：非分支指令
    val j               = Output(Bool())
    val jr              = Output(Bool())
    val jalr            = Output(Bool())
    val renx1           = Output(Bool())
    val alu             = Output(UInt(5.W))//alu操作类型，FN_X:无关
    val mul             = Output(Bool())//Y：乘法；N：非乘法；X：无关
    val div             = Output(Bool())//Y：除法；N：非除法；X：无关
    val mem_val         = Output(Bool())//Y：访问存储器（读或写）N：不访问存储器；X：无关
    val mem_cmd         = Output(UInt(2.W))//M_X 无关；M_RD:读存储器；M_WR:写存储器
    val mem_type        = Output(UInt(2.W))//MT_X：无关；MT_B：
    val mem_et          = Output(Bool())
    val renf1           = Output(Bool())//Y：浮点寄存器1可访问；N：浮点寄存器1不可访问；X：无关
    val renf2           = Output(Bool())//Y：浮点寄存器2可访问；N：浮点寄存器2不可访问；X：无关
    val renf3           = Output(Bool())//Y：浮点寄存器3可访问；N：浮点寄存器3不可访问；X：无关
    val rwen            = Output(Bool())//Y：写回寄存器文件；N：不写回寄存器文件；X：无关
    val wxd             = Output(UInt(3.W))////0:将存储器读出的结果写回寄存器；1:将ALU结果写回寄存器；WXD_X:无关项
    val cp0             = Output(UInt(2.W))//??
    val cp0_sel         = Output(UInt(3.W))
    val Regdst          = Output(Bool())//Y：写回rd；N：写回rt；X：无关
    val mul_divSign     = Output(Bool())//Y：有符号乘除法，取字节或半字时符号扩展；N：无符号乘除法，取字节或半字时零扩展；X：无关
    val op1             = Output(UInt(32.W))
    val busa            = Output(UInt(32.W))
    val busb            = Output(UInt(32.W))
    val rs              = Output(UInt(5.W))
    val rt              = Output(UInt(5.W))
    val rd              = Output(UInt(5.W))
    val imm26           = Output(UInt(26.W))
    val imm32           = Output(UInt(32.W))
    val s_alu2          = Output(UInt(3.W))
    val bp              = Output(Bool())
    val sys             = Output(Bool())
    val ri              = Output(Bool())
    val eret            = Output(Bool())
    val b               = Output(Bool())
    val cp0_read        = Output(Bool())
    val cp0_write       = Output(Bool())
    val cp0datOut       = Output(UInt(32.W))
    val cmp_out 		= Output(Bool())

    val LO            = Output(UInt(32.W))
    val HI            = Output(UInt(32.W))
}
class branch extends Bundle(){
    val in1         = Input(UInt(32.W))
    val in2         = Input(UInt(32.W))
}
class ID extends Module{
    val io  = IO(new Bundle{
        val out         = new IDEXOUT()
        val br         = new branch()

        val inst        = Input(UInt(32.W))
        val rf_busa     = Input(UInt(32.W))
        val rf_busb     = Input(UInt(32.W))
        val rf_LO       = Input(UInt(32.W))
        val rf_HI       = Input(UInt(32.W))
        val cp0dat      = Input(UInt(32.W))
        val br_j        = Output(Bool())

    })

    val cpath       = Module(new Cpath())
    //val sel         = io.inst(2,0)

    cpath.io.inst       := io.inst
    io.out.rs           := io.inst(25,21)
    io.out.rt           := io.inst(20,16)
    io.out.rd           := io.inst(15,11)
    io.out.ival         := cpath.io.ctrl.ival
    io.out.fp_val       := cpath.io.ctrl.fp_val
    io.out.alu          := cpath.io.ctrl.alu
    io.out.mul          := cpath.io.ctrl.mul
    io.out.div          := cpath.io.ctrl.div
    io.out.mem_val      := cpath.io.ctrl.mem_val
    io.out.mem_cmd      := cpath.io.ctrl.mem_cmd
    io.out.mem_type     := cpath.io.ctrl.mem_type
    io.out.mem_et       := cpath.io.ctrl.mem_et
    io.out.renf1        := cpath.io.ctrl.renf1
    io.out.renf2        := cpath.io.ctrl.renf2
    io.out.renf3        := cpath.io.ctrl.renf3
    io.out.rwen         := cpath.io.ctrl.rwen
    io.out.wxd          := cpath.io.ctrl.wxd
    io.out.Regdst       := cpath.io.ctrl.regdst
    io.out.mul_divSign  := cpath.io.ctrl.mul_divSign
    io.out.s_alu2       := cpath.io.ctrl.s_alu2
    io.out.bp           := cpath.io.ctrl.bp
    io.out.sys          := cpath.io.ctrl.sys
    io.out.ri           := cpath.io.ctrl.ri
    io.out.eret         := cpath.io.ctrl.eret
    io.out.cp0_sel      := io.inst(2,0)
    io.out.cp0          := cpath.io.ctrl.cp0
    io.out.b            := cpath.io.ctrl.b
    io.out.cp0_read     := cpath.io.ctrl.cp0_read
    io.out.cp0_write    := cpath.io.ctrl.cp0_write
    io.out.cmp_out		:= false.B
    io.br_j             := io.out.br || io.out.j || io.out.jr || io.out.jalr

    //io.out.rs         := io.rf_busa
    val br = cpath.io.ctrl.br
    val j = cpath.io.ctrl.j
    val jal = cpath.io.ctrl.jal
    val jalr = cpath.io.ctrl.jalr

    io.out.pcRegValid := false.B
    io.out.br         := false.B
    io.out.j          := false.B
    io.out.jr         := false.B
    io.out.jalr       := false.B
    io.out.renx1      := cpath.io.ctrl.renx1 //geng

    when(br){
    // when(io.out.alu === 27.U){
    //     logic := Mux( io.br.in1 ^ in2 === 0.U , 1.U,0.U)
    // }.elsewhen(io.out.alu === 28.U){
    //     logic := Mux( io.br.in1 ^ in2 === 0.U , 0.U,1.U)
    // }.elsewhen(io.out.alu === 21.U || io.out.alu === 26.U){
    //     logic := !(io.br.in1(31))
    // }.elsewhen(io.out.alu === 22.U){
    //     logic := !((io.br.in1(31)) || (adder_out === 0.U))
    // }.elsewhen(io.out.alu === 23.U){
    //     logic := (io.br.in1(31)) || (adder_out === 0.U)
    // }.elsewhen(io.out.alu === 24.U || io.out.alu === 25.U){
    //     logic := io.br.in1(31)
    // }.otherwise{
    //     logic := 0.U
    // }
    //     io.out.br := logic
    io.out.br := true.B
        when(!j){
            when(jal){
                io.out.pcRegValid := true.B
            }
        }
    }.otherwise{
        when(!j){
            when(jal){
                io.out.pcRegValid   := true.B
                io.out.j            := true.B
                }.otherwise{
                    when(jalr){
                        io.out.jalr         := true.B
                        io.out.jr           := true.B
                    }
                }
        }.otherwise{
            when(jal){
                io.out.jr       := true.B
            }otherwise{
                io.out.j        := true.B
            }
        }
    }


    // immediates
    val Imm16   = Wire(UInt(16.W))
    val Imm32   = Wire(UInt(32.W))
    val Imm26   = io.inst(25,0)

    io.out.imm26 := Imm26
    io.out.imm32 := Imm32

    io.out.LO := io.rf_LO
    io.out.HI := io.rf_HI
    io.out.cp0datOut := io.cp0dat

    Imm16       := io.inst(15, 0)
    when(cpath.io.ctrl.imm === IMM_SE)
    {
        Imm32 := Cat(Fill(16,Imm16(15)), Imm16)
    }
    when(cpath.io.ctrl.imm === IMM_ZE)
    {
        Imm32 := Cat(Fill(16,0.U), Imm16)
    }

    io.out.busa     := io.rf_busa
    io.out.busb     := io.rf_busb

    //op1
    when(cpath.io.ctrl.s_alu1 === A_RS){
        io.out.op1  := io.rf_busa
    }
    when(cpath.io.ctrl.s_alu1 === A_SA){
        io.out.op1  := io.inst(10, 6)
    }
    when(cpath.io.ctrl.s_alu1 === A_IMM){
        io.out.op1  := Cat(Imm16,Fill(16,0.U))
    }


    val busb_inv = Mux(isSub(cpath.io.ctrl.alu),~io.rf_busb,io.rf_busb)
    val busa_xor_busb = io.rf_busa ^  io.rf_busb
    val busb_cmplm_2 = busb_inv + isSub(cpath.io.ctrl.alu)
    // ADD, SUB
    val adder_out = io.rf_busa + busb_cmplm_2

  	when(cpath.io.ctrl.alu === FN_BEQ){
  		io.out.cmp_out := Mux( busa_xor_busb === 0.U , 1.U,0.U)
  	}.elsewhen(cpath.io.ctrl.alu === FN_BNE){
  		io.out.cmp_out := Mux( busa_xor_busb === 0.U , 0.U,1.U)
  	}.elsewhen(cpath.io.ctrl.alu === FN_BGEZ || cpath.io.ctrl.alu === FN_BGEZAL){
	    io.out.cmp_out := !(io.rf_busa(31))
  	}.elsewhen(cpath.io.ctrl.alu === FN_BGTZ){
  		io.out.cmp_out := !((io.rf_busa(31)) || (adder_out === 0.U))
  	}.elsewhen(cpath.io.ctrl.alu === FN_BLEZ){
    	io.out.cmp_out := (io.rf_busa(31)) || (adder_out === 0.U)
  	}.elsewhen(cpath.io.ctrl.alu === FN_BLTZ || cpath.io.ctrl.alu === FN_BLTZAL){
    	io.out.cmp_out := io.rf_busa(31)
  	}

  }
