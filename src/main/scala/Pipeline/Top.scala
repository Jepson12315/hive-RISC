package Pipeline

import chisel3._
import chisel3.util._
import chisel3.iotesters.Driver
import java.io.PrintWriter

class TopIO extends Bundle(){
    val reset            = Input(Bool())

    val if_ben          = Output(UInt(4.W))
    val if_wdata        = Output(UInt(32.W))
    val if_wr           = Output(Bool())
    val if_addr         = Output(UInt(32.W))
    val if_addr_ok      = Input(Bool())
    val if_data_ok      = Input(Bool())
    val if_rdata        = Input(UInt(32.W))

    val mem_ben          = Output(UInt(4.W))
    val mem_wdata        = Output(UInt(32.W))
    val mem_wr           = Output(Bool())
    val mem_addr         = Output(UInt(32.W))
    val mem_addr_ok      = Input(Bool())
    val mem_data_ok      = Input(Bool())
    val mem_rdata        = Input(UInt(32.W))

    //for test
    val MEM_WB_inst  = Output(UInt(32.W))
}


class Top extends Module(){
    val io            = IO(new TopIO())
    val Fr            = Module(new Front())
    val ID            = Module(new ID())
    val EXE           = Module(new EXE())
    val MEM           = Module(new MemTop())
    val WB            = Module(new Writeback())
    val RF            = Module(new RegFile())
    val risk          = Module(new Risk())
    val CP0           = Module(new CP0())


//part 1
    //io.if_ben        := Mux(EXE.io.div_busy || EXE.io.mul_busy, 0.U, 15.U)
    io.if_ben       := 15.U
    io.if_wdata      := 0.U
    io.if_wr         := false.B
    io.if_addr       := Fr.io.hand.if_addr
    Fr.io.hand.if_addr_ok:= io.if_addr_ok
    Fr.io.hand.if_data_ok:= io.if_data_ok
    Fr.io.hand.if_rdata  := io.if_rdata

    //val notOK  = RegInit(0.U(1.W))
    //notOK :=EXE.io.mem_notOK
    //io.mem_ben        := Mux(MEM.io.stop_ben === 1.U, 0.U, EXE.io.mem_ben)
    io.mem_ben        := EXE.io.mem_ben

    io.mem_wdata      := EXE.io.mem_wdata
    io.mem_wr         := EXE.io.mem_wr
    io.mem_addr       := EXE.io.mem_addr
    MEM.io.mem_addr_ok:= io.mem_addr_ok
    MEM.io.mem_data_ok:= io.mem_data_ok
    MEM.io.mem_rdata  := io.mem_rdata
    EXE.io.mem_addr_ok:= io.mem_addr_ok
    EXE.io.mem_data_ok:= io.mem_data_ok
    //EXE.io.mem_rdata  := io.mem_rdata

//part 2


    val W_Fr_ID_inst  = Wire(UInt(32.W))
    val W_Fr_ID_pcReg = Wire(UInt(32.W))
    val W_Fr_ID_pcbad = Wire(UInt(1.W))
    val W_Fr_ID_BD    = Wire(UInt(1.W))

    val W_ID_EXE_ival         = Wire(UInt(1.W))
    val W_ID_EXE_fp_val       = Wire(UInt(1.W))
    val W_ID_EXE_j            = Wire(UInt(1.W))
    val W_ID_EXE_jr           = Wire(UInt(1.W))
    val W_ID_EXE_br           = Wire(UInt(1.W))
    val W_ID_EXE_jalr         = Wire(UInt(1.W))
    val W_ID_EXE_alu          = Wire(UInt(5.W))
    val W_ID_EXE_mul          = Wire(UInt(1.W))
    val W_ID_EXE_div          = Wire(UInt(1.W))
    val W_ID_EXE_mem_val      = Wire(UInt(1.W))
    val W_ID_EXE_mem_cmd      = Wire(UInt(2.W))
    val W_ID_EXE_mem_type     = Wire(UInt(2.W))
    val W_ID_EXE_mem_et       = Wire(UInt(1.W))
    val W_ID_EXE_renf1        = Wire(UInt(1.W))
    val W_ID_EXE_renf2        = Wire(UInt(1.W))
    val W_ID_EXE_renf3        = Wire(UInt(1.W))
    val W_ID_EXE_rwen         = Wire(UInt(1.W)) //RegWrite
    val W_ID_EXE_wxd          = Wire(UInt(3.W))
    val W_ID_EXE_Regdst       = Wire(UInt(1.W))
    val W_ID_EXE_mul_divSign  = Wire(UInt(1.W))
    val W_ID_EXE_pcRegValid   = Wire(UInt(1.W))
    val W_ID_EXE_pcReg        = Wire(UInt(32.W))
    val W_ID_EXE_op1          = Wire(UInt(32.W))
    val W_ID_EXE_rd           = Wire(UInt(5.W))
    val W_ID_EXE_rt           = Wire(UInt(5.W))
    val W_ID_EXE_rs           = Wire(UInt(5.W))
    val W_ID_EXE_busa         = Wire(UInt(32.W))
    val W_ID_EXE_busb         = Wire(UInt(32.W))
    val W_ID_EXE_imm32        = Wire(UInt(32.W))
    val W_ID_EXE_s_alu2       = Wire(UInt(3.W))
    val W_ID_EXE_renx1        = Wire(UInt(1.W))
    //val W_ID_EXE_LO           = Wire(UInt(32.W))
    //val W_ID_EXE_HI           = Wire(UInt(32.W))
    val W_ID_EXE_bp           = Wire(UInt(1.W))
    val W_ID_EXE_sys          = Wire(UInt(1.W))
    val W_ID_EXE_ri           = Wire(UInt(1.W))
    val W_ID_EXE_eret         = Wire(UInt(1.W))
    val W_ID_EXE_BD           = Wire(UInt(1.W))
    val W_ID_EXE_pcbad        = Wire(UInt(1.W))
    val W_ID_EXE_cp0datOut    = Wire(UInt(32.W))
    val W_F_gpr_rs            = Wire(UInt(32.W))
    val W_ID_EXE_inst 		  = Wire(UInt(32.W))

    val W_EXE_MEM_HI              = Wire(UInt(32.W))//乘法高位；除法商
    val W_EXE_MEM_LO              = Wire(UInt(32.W))//乘法低位；除法余
    val W_EXE_MEM_ALUout          = Wire(UInt(32.W))//alu输出
    val W_EXE_MEM_ready           = Wire(UInt(1.W))//
    val W_EXE_MEM_ZeroException   = Wire(UInt(1.W))//
    val W_EXE_MEM_mem_val        = Wire(UInt(1.W))//1：问存储（或写）0：不访问存储；X：无
    val W_EXE_MEM_mem_cmd         = Wire(UInt(2.W))//M_X 无关0:读存储器1:写存储器
    val W_EXE_MEM_mem_type        = Wire(UInt(2.W))
    val W_EXE_MEM_rwen            = Wire(UInt(1.W))//?
    val W_EXE_MEM_wxd             = Wire(UInt(3.W))//0:将存储器读出的结果写回寄存器1:将ALU结果写回寄存；WXD_X:无关
    val W_EXE_MEM_Regdst          = Wire(UInt(1.W))//1：写回rd0：写回rt；X：无
    val W_EXE_MEM_datToMem        = Wire(UInt(32.W))//写入内存的数
    val W_EXE_MEM_rd              = Wire(UInt(5.W))
    val W_EXE_MEM_rt              = Wire(UInt(5.W))
    val W_EXE_MEM_rs              = Wire(UInt(5.W))
    val W_EXE_MEM_pcRegValid      = Wire(UInt(1.W))
    val W_EXE_MEM_pcReg           = Wire(UInt(32.W))
    val W_EXE_MEM_jalr            = Wire(UInt(1.W))
    val W_EXE_MEM_imm32           = Wire(UInt(32.W))
    val W_EXE_MEM_overflow        = Wire(UInt(1.W))
    val W_EXE_MEM_mem_et          = Wire(UInt(1.W))
    val W_EXE_MEM_BD              = Wire(UInt(1.W))
    val W_EXE_MEM_pcbad           = Wire(UInt(1.W))
    val W_EXE_MEM_bp              = Wire(UInt(1.W))
    val W_EXE_MEM_sys             = Wire(UInt(1.W))
    val W_EXE_MEM_ri              = Wire(UInt(1.W))
    val W_EXE_MEM_eret            = Wire(UInt(1.W))
    val W_EXE_MEM_cp0datOut       = Wire(UInt(32.W))
    val W_EXE_MEM_inst 			  = Wire(UInt(32.W))
    val W_EXE_MEM_ExcCode         = Wire(UInt(5.W))
    val W_EXE_MEM_AddrExc         = Wire(UInt(1.W))
    val W_EXE_MEM_BadAddr         = Wire(UInt(32.W))

//part 3
    val cmpout        = Wire(Bool())
    val gpr_rs        = Wire(UInt(32.W))
    gpr_rs  := RF.io.BusA
    val gpr_rt        = Wire(UInt(32.W))
    gpr_rt  := RF.io.BusB

    //regs : IF/ID
    val Fr_ID_inst    		= RegInit(0.U(32.W))
    val Fr_ID_pcReg   		= RegInit(0.U(32.W))
    val Fr_ID_pcbad   		= RegInit(0.U(1.W))
    val Fr_ID_BD            = RegInit(0.U(1.W))

    //regs : ID/EXE
    val ID_EXE_ival         = RegInit(0.U(1.W))
    val ID_EXE_fp_val       = RegInit(0.U(1.W))
    val ID_EXE_j            = RegInit(0.U(1.W))
    val ID_EXE_jr           = RegInit(0.U(1.W))
    val ID_EXE_br           = RegInit(0.U(1.W))
    val ID_EXE_jalr         = RegInit(0.U(1.W))
    val ID_EXE_alu          = RegInit(0.U(5.W))
    val ID_EXE_mul          = RegInit(0.U(1.W))
    val ID_EXE_div          = RegInit(0.U(1.W))
    val ID_EXE_mem_val      = RegInit(0.U(1.W))
    val ID_EXE_mem_cmd      = RegInit(0.U(2.W))
    val ID_EXE_mem_type     = RegInit(0.U(2.W))
    val ID_EXE_mem_et       = RegInit(0.U(1.W))
    val ID_EXE_renf1        = RegInit(0.U(1.W))
    val ID_EXE_renf2        = RegInit(0.U(1.W))
    val ID_EXE_renf3        = RegInit(0.U(1.W))
    val ID_EXE_rwen         = RegInit(0.U(1.W)) //RegWrite
    val ID_EXE_wxd          = RegInit(0.U(3.W))
    val ID_EXE_Regdst       = RegInit(0.U(1.W))
    val ID_EXE_mul_divSign  = RegInit(0.U(1.W))
    val ID_EXE_pcRegValid   = RegInit(0.U(1.W))
    val ID_EXE_pcReg        = RegInit(0.U(32.W))
    val ID_EXE_op1          = RegInit(0.U(32.W))
    val ID_EXE_rd           = RegInit(0.U(5.W))
    val ID_EXE_rt           = RegInit(0.U(5.W))
    val ID_EXE_rs           = RegInit(0.U(5.W))
    val ID_EXE_busa         = RegInit(0.U(32.W))
    val ID_EXE_busb         = RegInit(0.U(32.W))
    val ID_EXE_imm32        = RegInit(0.U(32.W))
    val ID_EXE_s_alu2       = RegInit(0.U(3.W))
    val ID_EXE_renx1        = RegInit(0.U(1.W))
    //val ID_EXE_LO           = RegInit(0.U(32.W))
    //val ID_EXE_HI           = RegInit(0.U(32.W))
    val ID_EXE_bp           = RegInit(0.U(1.W))
    val ID_EXE_sys          = RegInit(0.U(1.W))
    val ID_EXE_ri           = RegInit(0.U(1.W))
    val ID_EXE_eret         = RegInit(0.U(1.W))
    val ID_EXE_BD           = RegInit(0.U(1.W))
    val ID_EXE_pcbad        = RegInit(0.U(1.W))
    val ID_EXE_cp0datOut    = RegInit(0.U(32.W))
    val F_gpr_rs            = RegInit(0.U(32.W))
    val ID_EXE_inst 		= RegInit(0.U(32.W))


    //reg:EXE/MEM
    val EXE_MEM_HI              = RegInit(0.U(32.W))//乘法高位；除法商
    val EXE_MEM_LO              = RegInit(0.U(32.W))//乘法低位；除法余
    val EXE_MEM_ALUout          = RegInit(0.U(32.W))//alu输出
    val EXE_MEM_ready           = RegInit(0.U(1.W))//
    val EXE_MEM_ZeroException   = RegInit(0.U(1.W))//
    val EXE_MEM_mem_val         = RegInit(0.U(1.W))//1：问存储（或写）0：不访问存储；X：无
    val EXE_MEM_mem_cmd         = RegInit(0.U(2.W))//M_X 无关0:读存储器1:写存储器
    val EXE_MEM_mem_type        = RegInit(0.U(2.W))
    val EXE_MEM_rwen            = RegInit(0.U(1.W))//?
    val EXE_MEM_wxd             = RegInit(0.U(3.W))//0:将存储器读出的结果写回寄存器1:将ALU结果写回寄存；WXD_X:无关
    val EXE_MEM_Regdst          = RegInit(0.U(1.W))//1：写回rd0：写回rt；X：无
    val EXE_MEM_datToMem        = RegInit(0.U(32.W))//写入内存的数
    val EXE_MEM_rd              = RegInit(0.U(5.W))
    val EXE_MEM_rt              = RegInit(0.U(5.W))
    val EXE_MEM_rs              = RegInit(0.U(5.W))
    val EXE_MEM_pcRegValid      = RegInit(0.U(1.W))
    val EXE_MEM_pcReg           = RegInit(0.U(32.W))
    val EXE_MEM_jalr            = RegInit(0.U(1.W))
    val EXE_MEM_imm32           = RegInit(0.U(32.W))
    val EXE_MEM_overflow        = RegInit(0.U(1.W))
    val EXE_MEM_mem_et          = RegInit(0.U(1.W))
    val EXE_MEM_BD              = RegInit(0.U(1.W))
    val EXE_MEM_pcbad           = RegInit(0.U(1.W))
    val EXE_MEM_bp              = RegInit(0.U(1.W))
    val EXE_MEM_sys             = RegInit(0.U(1.W))
    val EXE_MEM_ri              = RegInit(0.U(1.W))
    val EXE_MEM_eret            = RegInit(0.U(1.W))
    val EXE_MEM_cp0datOut       = RegInit(0.U(32.W))
    val EXE_MEM_inst 			= RegInit(0.U(32.W))
    val EXE_MEM_ExcCode         = RegInit(0.U(5.W))
    val EXE_MEM_AddrExc         = RegInit(0.U(1.W))
    val EXE_MEM_BadAddr         = RegInit(0.U(32.W))

    //reg:MEM/WB
    val MEM_WB_HI           = RegInit(0.U(32.W))//乘法高位；除法商
    val MEM_WB_LO           = RegInit(0.U(32.W))//乘法低位；除法余
    val MEM_WB_WbEn         = RegInit(false.B)
    val MEM_WB_EX_out       = RegInit(0.U(32.W))
    val MEM_WB_dat_to_reg   = RegInit(0.U(32.W))
    val MEM_WB_reg_addr     = RegInit(0.U(32.W))
    val MEM_WB_wxd          = RegInit(0.U(3.W))
    val MEM_WB_rs           = RegInit(0.U(5.W))
    val MEM_WB_rd           = RegInit(0.U(5.W))
    val MEM_WB_rt           = RegInit(0.U(5.W))
    val MEM_WB_Regdst       = RegInit(false.B)
    val MEM_WB_pcReg        = RegInit(0.U(32.W))
    val MEM_WB_pcRegValid   = RegInit(0.U(1.W))
    val MEM_WB_jalr         = RegInit(0.U(1.W))
    val MEM_WB_BD           = RegInit(0.U(1.W))
    val MEM_WB_BadAddr      = RegInit(0.U(32.W))
    val MEM_WB_pcbad        = RegInit(0.U(1.W))
    val MEM_WB_overflow     = RegInit(0.U(1.W))
    val MEM_WB_ExcCode      = RegInit(0.U(5.W))
    val MEM_WB_bp           = RegInit(0.U(1.W))
    val MEM_WB_sys          = RegInit(0.U(1.W))
    val MEM_WB_ri           = RegInit(0.U(1.W))
    val MEM_WB_eret         = RegInit(0.U(1.W))
    val MEM_WB_AddrExc      = RegInit(0.U(1.W))
    val MEM_WB_exception    = RegInit(0.U(1.W))
    val MEM_WB_cp0datOut    = RegInit(0.U(32.W))
    val MEM_WB_cp0_write    = RegInit(0.U(1.W))
    val MEM_WB_inst 		= RegInit(0.U(32.W))

//part 4
    //IF/ID
    W_Fr_ID_inst := Fr_ID_inst
    W_Fr_ID_pcReg := Fr_ID_pcReg
    W_Fr_ID_pcbad := Fr_ID_pcbad
    W_Fr_ID_BD := Fr_ID_BD

    when(!risk.io.IDEN)
    {
    Fr_ID_inst  := Fr.io.ctr.inst
    Fr_ID_pcReg := Fr.io.ctr.pcReg
    Fr_ID_pcbad := Fr.io.ctr.pcbad
    Fr_ID_BD  := Fr.io.BD
    }.otherwise{
    Fr_ID_inst       := W_Fr_ID_inst
    Fr_ID_pcReg      := W_Fr_ID_pcReg
    Fr_ID_pcbad      := W_Fr_ID_pcbad
    Fr_ID_BD       := W_Fr_ID_BD

    }

    //ID/EXE
    W_ID_EXE_ival         := ID_EXE_ival
    W_ID_EXE_fp_val       := ID_EXE_fp_val
    W_ID_EXE_j            := ID_EXE_j
    W_ID_EXE_jr           := ID_EXE_jr
    W_ID_EXE_br           := ID_EXE_br
    W_ID_EXE_jalr         := ID_EXE_jalr
    W_ID_EXE_alu          := ID_EXE_alu
    W_ID_EXE_mul          := ID_EXE_mul
    W_ID_EXE_div          := ID_EXE_div
    W_ID_EXE_mem_val      := ID_EXE_mem_val
    W_ID_EXE_mem_cmd      := ID_EXE_mem_cmd
    W_ID_EXE_mem_type     := ID_EXE_mem_type
    W_ID_EXE_mem_et       := ID_EXE_mem_et
    W_ID_EXE_renf1        := ID_EXE_renf1
    W_ID_EXE_renf2        := ID_EXE_renf2
    W_ID_EXE_renf3        := ID_EXE_renf3
    W_ID_EXE_rwen         := ID_EXE_rwen
    W_ID_EXE_wxd          := ID_EXE_wxd
    W_ID_EXE_Regdst       := ID_EXE_Regdst
    W_ID_EXE_mul_divSign  := ID_EXE_mul_divSign
    W_ID_EXE_pcRegValid   := ID_EXE_pcRegValid
    W_ID_EXE_pcReg        := ID_EXE_pcReg
    W_ID_EXE_op1          := ID_EXE_op1
    W_ID_EXE_rd           := ID_EXE_rd
    W_ID_EXE_rt           := ID_EXE_rt
    W_ID_EXE_rs           := ID_EXE_rs
    W_ID_EXE_busa         := ID_EXE_busa
    W_ID_EXE_busb         := ID_EXE_busb
    W_ID_EXE_imm32        := ID_EXE_imm32
    W_ID_EXE_s_alu2       := ID_EXE_s_alu2
    W_ID_EXE_renx1        := ID_EXE_renx1
    //W_ID_EXE_LO           := ID_EXE_LO
    //W_ID_EXE_HI           := ID_EXE_HI
    W_ID_EXE_bp           := ID_EXE_bp
    W_ID_EXE_sys          := ID_EXE_sys
    W_ID_EXE_ri           := ID_EXE_ri
    W_ID_EXE_eret         := ID_EXE_eret
    W_ID_EXE_BD           := ID_EXE_BD
    W_ID_EXE_pcbad        := ID_EXE_pcbad
    W_ID_EXE_cp0datOut    := ID_EXE_cp0datOut
    W_F_gpr_rs            := F_gpr_rs
    W_ID_EXE_inst 		  := ID_EXE_inst

    when(!risk.io.EXEN){

    ID_EXE_ival             := ID.io.out.ival
    ID_EXE_fp_val           := ID.io.out.fp_val
    ID_EXE_j                := ID.io.out.j
    ID_EXE_jr               := ID.io.out.jr
    ID_EXE_br               := ID.io.out.br
    ID_EXE_jalr             := ID.io.out.jalr
    ID_EXE_alu              := ID.io.out.alu
    ID_EXE_mul              := ID.io.out.mul
    ID_EXE_div              := ID.io.out.div
    ID_EXE_mem_val          := ID.io.out.mem_val
    ID_EXE_mem_cmd          := ID.io.out.mem_cmd
    ID_EXE_mem_type         := ID.io.out.mem_type
    ID_EXE_mem_et           := ID.io.out.mem_et
    ID_EXE_renf1            := ID.io.out.renf1
    ID_EXE_renf2            := ID.io.out.renf2
    ID_EXE_renf3            := ID.io.out.renf3
    ID_EXE_rwen             := ID.io.out.rwen
    ID_EXE_wxd              := ID.io.out.wxd
    ID_EXE_Regdst           := ID.io.out.Regdst
    ID_EXE_mul_divSign      := ID.io.out.mul_divSign
    ID_EXE_op1              := ID.io.out.op1
    ID_EXE_pcRegValid       := ID.io.out.pcRegValid
    ID_EXE_pcReg            := Fr_ID_pcReg
    ID_EXE_rd               := ID.io.out.rd
    ID_EXE_rt               := ID.io.out.rt
    ID_EXE_rs               := ID.io.out.rs
    ID_EXE_ri               := ID.io.out.ri
    ID_EXE_sys              := ID.io.out.sys
    ID_EXE_bp               := ID.io.out.bp
    ID_EXE_eret             := ID.io.out.eret
    ID_EXE_BD               := Mux(Fr_ID_BD === 1.U || ID_EXE_br === 1.U||ID_EXE_jalr === 1.U ||ID_EXE_j === 1.U || ID_EXE_jr === 1.U, 1.U, 0.U)
    ID_EXE_pcbad            := Fr_ID_pcbad
    ID_EXE_busb             := ID.io.out.busb
    ID_EXE_imm32            := ID.io.out.imm32
    ID_EXE_s_alu2           := ID.io.out.s_alu2
    ID_EXE_renx1            := ID.io.out.renx1
    //ID_EXE_LO               := ID.io.out.LO
    //ID_EXE_HI               := ID.io.out.HI
    ID_EXE_mem_et           := ID.io.out.mem_et
    ID_EXE_cp0datOut        := ID.io.out.cp0datOut
    ID_EXE_inst 			:= Fr_ID_inst
    F_gpr_rs    			:= gpr_rs

    }.otherwise{
    //ID→ID/EXE
    ID_EXE_ival         := W_ID_EXE_ival
    ID_EXE_fp_val       := W_ID_EXE_fp_val
    ID_EXE_j            := W_ID_EXE_j
    ID_EXE_jr           := W_ID_EXE_jr
    ID_EXE_br           := W_ID_EXE_br
    ID_EXE_jalr         := W_ID_EXE_jalr
    ID_EXE_alu          := W_ID_EXE_alu
    ID_EXE_mul          := W_ID_EXE_mul
    ID_EXE_div          := W_ID_EXE_div
    ID_EXE_mem_val      := W_ID_EXE_mem_val
    ID_EXE_mem_cmd      := W_ID_EXE_mem_cmd
    ID_EXE_mem_type     := W_ID_EXE_mem_type
    ID_EXE_mem_et       := W_ID_EXE_mem_et
    ID_EXE_renf1        := W_ID_EXE_renf1
    ID_EXE_renf2        := W_ID_EXE_renf2
    ID_EXE_renf3        := W_ID_EXE_renf3
    ID_EXE_rwen         := W_ID_EXE_rwen
    ID_EXE_wxd          := W_ID_EXE_wxd
    ID_EXE_Regdst       := W_ID_EXE_Regdst
    ID_EXE_mul_divSign  := W_ID_EXE_mul_divSign
    ID_EXE_pcRegValid   := W_ID_EXE_pcRegValid
    ID_EXE_pcReg        := W_ID_EXE_pcReg
    ID_EXE_op1          := W_ID_EXE_op1
    ID_EXE_rd           := W_ID_EXE_rd
    ID_EXE_rt           := W_ID_EXE_rt
    ID_EXE_rs           := W_ID_EXE_rs
    ID_EXE_busa         := W_ID_EXE_busa
    ID_EXE_busb         := W_ID_EXE_busb
    ID_EXE_imm32        := W_ID_EXE_imm32
    ID_EXE_s_alu2       := W_ID_EXE_s_alu2
    ID_EXE_renx1        := W_ID_EXE_renx1
    //ID_EXE_LO           := W_ID_EXE_LO
    //ID_EXE_HI           := W_ID_EXE_HI
    ID_EXE_bp           := W_ID_EXE_bp
    ID_EXE_sys          := W_ID_EXE_sys
    ID_EXE_ri           := W_ID_EXE_ri
    ID_EXE_eret         := W_ID_EXE_eret
    ID_EXE_BD           := W_ID_EXE_BD
    ID_EXE_pcbad        := W_ID_EXE_pcbad
    ID_EXE_cp0datOut    := W_ID_EXE_cp0datOut
    F_gpr_rs            := W_F_gpr_rs
    ID_EXE_inst 		:= W_ID_EXE_inst
    }




    //EXE/MEM
    W_EXE_MEM_HI              := EXE_MEM_HI
    W_EXE_MEM_LO              := EXE_MEM_LO
    W_EXE_MEM_ALUout          := EXE_MEM_ALUout
    W_EXE_MEM_ready           := EXE_MEM_ready
    W_EXE_MEM_ZeroException   := EXE_MEM_ZeroException
    W_EXE_MEM_mem_val         := EXE_MEM_mem_val
    W_EXE_MEM_mem_cmd         := EXE_MEM_mem_cmd
    W_EXE_MEM_mem_type        := EXE_MEM_mem_type
    W_EXE_MEM_rwen            := EXE_MEM_rwen
    W_EXE_MEM_wxd             := EXE_MEM_wxd
    W_EXE_MEM_Regdst          := EXE_MEM_Regdst
    W_EXE_MEM_datToMem        := EXE_MEM_datToMem
    W_EXE_MEM_rd              := EXE_MEM_rd
    W_EXE_MEM_rt              := EXE_MEM_rt
    W_EXE_MEM_rs              := EXE_MEM_rs
    W_EXE_MEM_pcRegValid      := EXE_MEM_pcRegValid
    W_EXE_MEM_pcReg           := EXE_MEM_pcReg
    W_EXE_MEM_jalr            := EXE_MEM_jalr
    W_EXE_MEM_imm32           := EXE_MEM_imm32
    W_EXE_MEM_overflow        := EXE_MEM_overflow
    W_EXE_MEM_mem_et          := EXE_MEM_mem_et

    W_EXE_MEM_BD              := EXE_MEM_BD
    W_EXE_MEM_pcbad           := EXE_MEM_pcbad
    W_EXE_MEM_bp              := EXE_MEM_bp
    W_EXE_MEM_sys             := EXE_MEM_sys
    W_EXE_MEM_ri              := EXE_MEM_ri
    W_EXE_MEM_eret            := EXE_MEM_eret
    W_EXE_MEM_cp0datOut       := EXE_MEM_cp0datOut
    W_EXE_MEM_inst 			  := EXE_MEM_inst
    when(!risk.io.MEMEN){

    EXE_MEM_HI                  := ID.io.out.HI
    EXE_MEM_LO                  := ID.io.out.LO
    EXE_MEM_ALUout              := EXE.io.ALUout
    EXE_MEM_ZeroException       := EXE.io.ZeroException
    EXE_MEM_mem_val             := ID_EXE_mem_val
    EXE_MEM_mem_cmd             := ID_EXE_mem_cmd
    EXE_MEM_mem_type            := ID_EXE_mem_type
    EXE_MEM_rwen                := Mux(EXE.io.overflow,0.U,ID_EXE_rwen)
    EXE_MEM_wxd                 := ID_EXE_wxd
    EXE_MEM_Regdst              := ID_EXE_Regdst
    EXE_MEM_datToMem            := EXE.io.datatomem
    EXE_MEM_overflow            := EXE.io.overflow
    EXE_MEM_mem_et              := ID_EXE_mem_et
    EXE_MEM_rs                  := ID_EXE_rs
    EXE_MEM_rd                  := ID_EXE_rd
    EXE_MEM_rt                  := ID_EXE_rt
    EXE_MEM_pcRegValid          := ID_EXE_pcRegValid
    EXE_MEM_pcReg               := ID_EXE_pcReg
    EXE_MEM_jalr                := ID_EXE_jalr
    EXE_MEM_imm32               := ID_EXE_imm32
    EXE_MEM_bp                  := ID_EXE_bp
    EXE_MEM_sys                 := ID_EXE_sys
    EXE_MEM_ri                  := ID_EXE_ri
    EXE_MEM_BD                  := ID_EXE_BD
    EXE_MEM_pcbad               := ID_EXE_pcbad
    EXE_MEM_eret                := ID_EXE_eret
    EXE_MEM_cp0datOut           := ID_EXE_cp0datOut
    EXE_MEM_inst 				:= ID_EXE_inst
    EXE_MEM_BadAddr             := EXE.io.BadVAddr
    EXE_MEM_ExcCode             := EXE.io.ExcCode
    EXE_MEM_AddrExc             := EXE.io.AddrExc

    }.otherwise{

    EXE_MEM_HI              := W_EXE_MEM_HI
    EXE_MEM_LO              := W_EXE_MEM_LO
    EXE_MEM_ALUout          := W_EXE_MEM_ALUout
    EXE_MEM_ready           := W_EXE_MEM_ready
    EXE_MEM_ZeroException   := W_EXE_MEM_ZeroException
    EXE_MEM_mem_val         := W_EXE_MEM_mem_val
    EXE_MEM_mem_cmd         := W_EXE_MEM_mem_cmd
    EXE_MEM_mem_type        := W_EXE_MEM_mem_type
    EXE_MEM_rwen            := W_EXE_MEM_rwen
    EXE_MEM_wxd             := W_EXE_MEM_wxd
    EXE_MEM_Regdst          := W_EXE_MEM_Regdst
    EXE_MEM_datToMem        := W_EXE_MEM_datToMem
    EXE_MEM_rd              := W_EXE_MEM_rd
    EXE_MEM_rt              := W_EXE_MEM_rt
    EXE_MEM_rs              := W_EXE_MEM_rs
    EXE_MEM_pcRegValid      := W_EXE_MEM_pcRegValid
    EXE_MEM_pcReg           := W_EXE_MEM_pcReg
    EXE_MEM_jalr            := W_EXE_MEM_jalr
    EXE_MEM_imm32           := W_EXE_MEM_imm32
    EXE_MEM_overflow        := W_EXE_MEM_overflow
    EXE_MEM_mem_et          := W_EXE_MEM_mem_et
    EXE_MEM_BD              := W_EXE_MEM_BD
    EXE_MEM_pcbad           := W_EXE_MEM_pcbad
    EXE_MEM_bp              := W_EXE_MEM_bp
    EXE_MEM_sys             := W_EXE_MEM_sys
    EXE_MEM_ri              := W_EXE_MEM_ri
    EXE_MEM_eret            := W_EXE_MEM_eret
    EXE_MEM_cp0datOut       := W_EXE_MEM_cp0datOut
    EXE_MEM_inst 			:= W_EXE_MEM_inst

    }

	//MEM/WB
    MEM_WB_WbEn             := Mux(EXE_MEM_AddrExc === 1.U || EXE_MEM_pcbad === 1.U || EXE_MEM_overflow === 1.U,0.U,EXE_MEM_rwen)
    MEM_WB_EX_out           := EXE_MEM_ALUout
    MEM_WB_dat_to_reg       := MEM.io.dat_to_reg
    MEM_WB_wxd              := EXE_MEM_wxd
    MEM_WB_rs               := EXE_MEM_rs
    MEM_WB_rd               := EXE_MEM_rd
    MEM_WB_rt               := EXE_MEM_rt
    MEM_WB_Regdst           := EXE_MEM_Regdst
    MEM_WB_HI               := EXE_MEM_HI
    MEM_WB_LO               := EXE_MEM_LO
    MEM_WB_pcReg            := EXE_MEM_pcReg
    MEM_WB_pcRegValid       := EXE_MEM_pcRegValid
    MEM_WB_jalr             := EXE_MEM_jalr
    MEM_WB_bp               := EXE_MEM_bp
    MEM_WB_sys              := EXE_MEM_sys
    MEM_WB_ri               := EXE_MEM_ri
    MEM_WB_BD               := EXE_MEM_BD
    MEM_WB_pcbad            := EXE_MEM_pcbad
    MEM_WB_BadAddr          := EXE_MEM_BadAddr
    MEM_WB_ExcCode          := EXE_MEM_ExcCode
    MEM_WB_AddrExc          := EXE_MEM_AddrExc
    MEM_WB_eret             := EXE_MEM_eret
    MEM_WB_overflow         := EXE_MEM_overflow
    MEM_WB_cp0datOut        := EXE_MEM_cp0datOut
    MEM_WB_inst 			:= EXE_MEM_inst



//part 6


	//IF Module Input signals
	Fr.io.hand.boot := ~io.reset
	Fr.io.instClr 	:= false.B
    Fr.io.epc 		:= CP0.io.EPC
    Fr.io.pcentry 	:= CP0.io.SolveEntry
    Fr.io.pcback 	:= CP0.io.PC_back
    Fr.io.edone 	:= CP0.io.done
    Fr.io.StallIF   := risk.io.IFEN
    Fr.io.Imm26     := ID.io.out.imm26
    Fr.io.j         := ID.io.out.j
    Fr.io.jr        := ID.io.out.jr
    Fr.io.br        := ID.io.out.br && cmpout === 1.U
    Fr.io.Imm32     := ID.io.out.imm32
    Fr.io.gpr_rs    := gpr_rs
    Fr.io.pcjump    := Fr_ID_pcReg
    Fr.io.pcbranch  := Fr_ID_pcReg
    Fr.io.b         := ID.io.out.b
    Fr.io.bimm32    := ID.io.out.imm32
    Fr.io.br_j      := ID.io.br_j
    Fr.io.dmem_notok := EXE.io.mem_notOK

    //ID Module Input signals
    ID.io.inst:= Fr_ID_inst
    ID.io.rf_busa           := gpr_rs
    ID.io.rf_busb           := gpr_rt
    ID.io.rf_LO             := RF.io.LO_out
    ID.io.rf_HI             := RF.io.HI_out
    ID.io.cp0dat        	:= CP0.io.CP0_datOut

    //Regfile Module Input signals

    RF.io.BusW              := WB.io.rf_busw
    RF.io.RS                := ID.io.out.rs
    RF.io.RT                := ID.io.out.rt
    RF.io.RD                := WB.io.rf_addr
    RF.io.RegWr             := WB.io.rf_wr
    RF.io.HI_in             := EXE.io.HI
    RF.io.LO_in             := EXE.io.LO
    RF.io.LO_wr             := EXE.io.LO_wr
    RF.io.HI_wr             := EXE.io.HI_wr
    RF.io.reset             := io.reset

    //EXE Module Input signals
    EXE.io.in1              := ID_EXE_op1
    EXE.io.busb             := ID_EXE_busb
    EXE.io.ALU_fn           := ID_EXE_alu
    EXE.io.mul              := ID_EXE_mul
    EXE.io.div              := ID_EXE_div
    EXE.io.mul_divsign      := ID_EXE_mul_divSign
    EXE.io.s_alu2           := ID_EXE_s_alu2
    EXE.io.imm32            := ID_EXE_imm32
    EXE.io.wxd              := ID_EXE_wxd
    EXE.io.renx1            := ID_EXE_renx1
    EXE.io.RS               := F_gpr_rs
    EXE.io.MemEn            := ID_EXE_mem_val
    EXE.io.mem_cmd          := ID_EXE_mem_cmd
    EXE.io.mem_type         := ID_EXE_mem_type
    //EXE.io.dat_to_mem       := EXE_MEM_datToMem
    //EXE.io.dmem_addr_In     := EXE_MEM_ALUout
    EXE.io.mem_et           := ID_EXE_mem_et



    //MEM Module Input signals
    MEM.io.MemEn            := EXE_MEM_mem_val
    MEM.io.mem_cmd          := EXE_MEM_mem_cmd
    MEM.io.mem_type         := EXE_MEM_mem_type
    //MEM.io.dat_to_mem       := EXE_MEM_datToMem
    MEM.io.dmem_addr_In     := EXE_MEM_ALUout
    MEM.io.mem_et           := EXE_MEM_mem_et



    //MEM→WB的信

    WB.io.jalr              := MEM_WB_jalr
    WB.io.WbEn              := MEM_WB_WbEn
    WB.io.EX_out            := MEM_WB_EX_out
    WB.io.dat_to_reg        := Mux(MEM_WB_wxd === 2.U,RF.io.HI_out,Mux(MEM_WB_wxd === 3.U ,RF.io.LO_out,Mux(MEM_WB_wxd === 4.U ,MEM_WB_cp0datOut , MEM_WB_dat_to_reg)))
    WB.io.wxd               := MEM_WB_wxd
    WB.io.Regdst            := MEM_WB_Regdst
    WB.io.rf_rd_in          := MEM_WB_rd
    WB.io.rf_rt_in          := MEM_WB_rt
    WB.io.EX_LO             := MEM_WB_LO
    WB.io.EX_HI             := MEM_WB_HI
    WB.io.pcRegValid        := MEM_WB_pcRegValid
    WB.io.pcReg             := MEM_WB_pcReg




//part 5

    when(risk.io.ForwardAE2){
        when(EXE_MEM_wxd === 2.U){
            EXE.io.in1 := EXE_MEM_HI
        }.elsewhen(EXE_MEM_wxd === 3.U){
            EXE.io.in1 := EXE_MEM_LO
        }.elsewhen(EXE_MEM_wxd === 4.U){
            EXE.io.in1 := EXE_MEM_cp0datOut
        }.otherwise{
        EXE.io.in1 := EXE_MEM_ALUout
        }
    }.elsewhen(risk.io.ForwardAE1){
        EXE.io.in1 := WB.io.rf_busw
    }

    when(risk.io.ForwardAE5){
        gpr_rs := EXE.io.ALUout
    }.elsewhen(risk.io.ForwardAE4){
        when(EXE_MEM_wxd === 2.U){
            gpr_rs := EXE_MEM_HI //RF.io.HI_out
        }.elsewhen(EXE_MEM_wxd === 3.U){
            gpr_rs := EXE_MEM_LO //RF.io.LO_out
        }.elsewhen(EXE_MEM_wxd === 4.U){
            gpr_rs := EXE_MEM_cp0datOut //CP0.io.CP0_datOut
        }.otherwise{
        gpr_rs := Mux(EXE_MEM_mem_val === 1.U ,MEM.io.dat_to_reg, EXE_MEM_ALUout)
    }
    }.elsewhen(risk.io.ForwardAE3 && !risk.io.EXEN){
        ID_EXE_op1 := WB.io.rf_busw
        gpr_rs := WB.io.rf_busw
    }


    when(risk.io.ForwardBE2){
        when(EXE_MEM_wxd === 2.U){
            EXE.io.busb := EXE_MEM_HI
        }.elsewhen(EXE_MEM_wxd === 3.U){
            EXE.io.busb := EXE_MEM_LO
        }.elsewhen(EXE_MEM_wxd === 4.U){
            EXE.io.busb := EXE_MEM_cp0datOut
        }.otherwise{
        EXE.io.busb := EXE_MEM_ALUout
        }
    }.elsewhen(risk.io.ForwardBE1){
        EXE.io.busb := WB.io.rf_busw
    }

    when(risk.io.ForwardBE5){
        gpr_rt := EXE.io.ALUout
    }.elsewhen(risk.io.ForwardBE4){
        when(EXE_MEM_wxd === 2.U){
            gpr_rt := EXE_MEM_HI
        }.elsewhen(EXE_MEM_wxd === 3.U){
            gpr_rt := EXE_MEM_LO
        }.elsewhen(EXE_MEM_wxd === 4.U){
            gpr_rt := EXE_MEM_cp0datOut
        }.otherwise{
        gpr_rt := Mux(EXE_MEM_mem_val === 1.U ,MEM.io.dat_to_reg, EXE_MEM_ALUout)
    }
        //gpr_rt := Mux(EXE_MEM_mem_val === 1.U ,MEM.io.dat_to_reg, EXE_MEM_ALUout)
    }.elsewhen(risk.io.ForwardBE3 && !risk.io.EXEN){
        ID_EXE_busb := WB.io.rf_busw
        gpr_rt := WB.io.rf_busw
    }


//part 9 :risk
    val rsE     = Wire(UInt(5.W))
    rsE := ID_EXE_rs
    val rtE     = Wire(UInt(5.W))
    rtE := ID_EXE_rt
    val rdE     = Wire(UInt(5.W))
    rdE := ID_EXE_rd
    val rsD     = ID.io.out.rs
    val rtD     = ID.io.out.rt
    val rdD     = ID.io.out.rd

    cmpout          := ID.io.out.cmp_out
    risk.io.RtE 	:= rtE
    risk.io.RsE 	:= rsE
    risk.io.RdE 	:= rdE
    risk.io.RsD 	:= rsD
    risk.io.RtD 	:= rtD
    risk.io.RdD 	:= rdD
    risk.io.WriteregM := Mux(EXE_MEM_pcRegValid === 1.U,31.U,Mux(EXE_MEM_Regdst === 1.U,EXE_MEM_rd,EXE_MEM_rt))
    risk.io.WriteregW := Mux(MEM_WB_pcRegValid === 1.U,31.U,Mux(MEM_WB_Regdst === 1.U ,MEM_WB_rd,MEM_WB_rt))
    risk.io.WriteregE := Mux(ID_EXE_pcRegValid === 1.U,31.U,Mux(ID_EXE_Regdst === 1.U ,ID_EXE_rd,ID_EXE_rt))
    risk.io.div := ID_EXE_div
    risk.io.brM := ID.io.out.br
    risk.io.comp_out := ID.io.out.cmp_out
    risk.io.mem_cmd  := ID_EXE_mem_cmd
    risk.io.mem_cmdM := EXE_MEM_mem_cmd
    risk.io.mem_val  := ID_EXE_mem_val
    risk.io.mem_valM := EXE_MEM_mem_val
    risk.io.rwenM    := EXE_MEM_rwen
    risk.io.rwenW    := MEM_WB_WbEn
    risk.io.rwenE    := ID_EXE_rwen
    risk.io.jD       := (ID.io.out.j)||(ID.io.out.jr)
    risk.io.ready    := EXE_MEM_ready
    //mem related
    risk.io.div_busy := EXE.io.div_busy
    risk.io.mul_busy := EXE.io.mul_busy
    //risk.io.dmem_notOK :=0.U
    risk.io.imem_notOK :=Fr.io.imem_notOK
    risk.io.dmem_notOK :=EXE.io.mem_notOK

    when(risk.io.EXECLR){
        ID_EXE_mem_val          := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_br               := 0.U
        ID_EXE_alu              := 0.U
        ID_EXE_mul              := 0.U
        ID_EXE_div              := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_op1              := 0.U
        ID_EXE_rd               := 0.U
        ID_EXE_rt               := 0.U
        //ID_EXE_rs               := 0.U
        ID_EXE_busb             := 0.U
        ID_EXE_imm32            := 0.U
        ID_EXE_s_alu2           := 0.U
        ID_EXE_inst             := 0.U
        ID_EXE_pcReg            := 0.U

    }
    when(risk.io.IDCLR){
        Fr_ID_inst := 0.U
        Fr_ID_pcReg := 0.U
        Fr_ID_pcbad := 0.U
    }


//part 7 :CP0

    CP0.io.sel          := ID.io.out.cp0_sel
    CP0.io.CP0_write    := ID.io.out.cp0_write
    CP0.io.CP0_read     := ID.io.out.cp0_read
    CP0.io.CP0_datIn    := gpr_rt
    CP0.io.CP0_addr     := ID.io.out.rd
    CP0.io.BD           := MEM_WB_BD
    CP0.io.ExceptionAddr:= MEM_WB_pcReg
    CP0.io.SolveBack    := MEM_WB_eret
    CP0.io.reset        := io.reset
    when(MEM_WB_pcbad === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := 4.U
        CP0.io.Badaddr := MEM_WB_pcReg
    }.elsewhen(MEM_WB_ri === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := 10.U
    }.elsewhen(MEM_WB_overflow === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := 12.U
    }.elsewhen(MEM_WB_bp === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := 9.U
    }.elsewhen(MEM_WB_sys === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := 8.U
    }.elsewhen(MEM_WB_AddrExc === 1.U){
        CP0.io.Exception := true.B
        CP0.io.ExceptionType := MEM_WB_ExcCode
        CP0.io.Badaddr := MEM_WB_BadAddr
    }.otherwise{
        CP0.io.Exception := false.B
    }

//part 8 :hazards come out with Exception
    when( Fr_ID_pcbad === 1.U || ID_EXE_pcbad===1.U || EXE_MEM_pcbad === 1.U || MEM_WB_pcbad ===1.U){
        Fr.io.instClr := true.B
    }


    when(ID.io.out.sys||ID.io.out.bp||ID.io.out.ri||ID.io.out.eret){
        Fr.io.instClr := true.B

        Fr_ID_inst := 0.U
        Fr_ID_pcbad := 0.U
        Fr_ID_pcReg := 0.U
    }
    when(ID_EXE_sys === 1.U||ID_EXE_bp ===1.U||ID_EXE_ri === 1.U||ID_EXE_eret === 1.U){
        Fr.io.instClr := true.B

        Fr_ID_inst := 0.U
        Fr_ID_pcbad := 0.U
        Fr_ID_pcReg := 0.U
    }
    when(EXE_MEM_sys === 1.U||EXE_MEM_bp ===1.U||EXE_MEM_ri === 1.U||EXE_MEM_eret === 1.U){
        Fr.io.instClr := true.B

        Fr_ID_inst := 0.U
        Fr_ID_pcbad := 0.U
        Fr_ID_pcReg := 0.U
    }
    when(MEM_WB_sys === 1.U||MEM_WB_bp ===1.U||MEM_WB_ri === 1.U||MEM_WB_eret === 1.U){
        Fr.io.instClr := true.B

        Fr_ID_inst := 0.U
        Fr_ID_pcbad := 0.U
        Fr_ID_pcReg := 0.U
    }
    when(EXE.io.overflow||EXE_MEM_overflow === 1.U||MEM_WB_overflow === 1.U){
        Fr.io.instClr := true.B

        Fr_ID_inst := 0.U
        Fr_ID_pcbad := 0.U
        Fr_ID_pcReg := 0.U

        ID_EXE_mem_val          := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_br               := 0.U
        ID_EXE_alu              := 0.U
        ID_EXE_mul              := 0.U
        ID_EXE_div              := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_op1              := 0.U
        ID_EXE_rd               := 0.U
        ID_EXE_rt               := 0.U
   //     ID_EXE_rs               := 0.U
        ID_EXE_busb             := 0.U
        ID_EXE_imm32            := 0.U
        ID_EXE_s_alu2           := 0.U
    }
    when(EXE.io.AddrExc||EXE_MEM_AddrExc === 1.U||MEM_WB_AddrExc === 1.U){
        Fr.io.instClr := true.B

        Fr_ID_inst                  := 0.U
        Fr_ID_pcReg                 := 0.U
        Fr_ID_pcbad                 := 0.U

        ID_EXE_mem_val          := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_br               := 0.U
        ID_EXE_alu              := 0.U
        ID_EXE_mul              := 0.U
        ID_EXE_div              := 0.U
        ID_EXE_rwen             := 0.U
        ID_EXE_op1              := 0.U
        ID_EXE_rd               := 0.U
        ID_EXE_rt               := 0.U
    //    ID_EXE_rs               := 0.U
        ID_EXE_busb             := 0.U
        ID_EXE_imm32            := 0.U
        ID_EXE_s_alu2           := 0.U
    }




    io.MEM_WB_inst := MEM_WB_inst               //only for test
}
