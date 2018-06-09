package Pipeline

import chisel3._
import chisel3.util._
import scala.math._

class handIO extends Bundle(){
    val boot            = Input(Bool())

    //val if_ben          = Output(UInt(4.W))
    //val if_wdata        = Output(UInt(32.W))
    //val if_wr           = Output(Bool())
    val if_addr         = Output(UInt(32.W))//inst addr
    val if_addr_ok      = Input(Bool())
    val if_data_ok      = Input(Bool())
    val if_rdata        = Input(UInt(32.W))//inst
}


class IFIO extends Bundle(){
    val inst            =   Output(UInt(32.W))
    val pcReg           =   Output(UInt(32.W))
    val pcbad           =   Output(Bool())
}

class pcIO extends Bundle(){
    val ctr             = new IFIO()
    val hand            = new handIO()

    val StallIF         =   Input(UInt(1.W))// stall
    val instClr         =   Input(Bool())
    val epc             =   Input(Bool())
    val pcentry         =   Input(UInt(32.W))
    val gpr_rs          =   Input(UInt(32.W)) //data of GPR[rs],used by jr,jalr
    val edone           =   Input(Bool())
    val pcback          =   Input(UInt(32.W))
    val Imm32           =   Input(UInt(32.W))
    val Imm26           =   Input(UInt(26.W))
    val br              =   Input(Bool())
    val j               =   Input(Bool())
    val jr              =   Input(Bool())
    val pcjump          =   Input(UInt(32.W))
    val pcbranch        =   Input(UInt(32.W))
    val b               =   Input(Bool())
    val bimm32          =   Input(UInt(32.W))
        //yuling
    val dmem_notok      =   Input(Bool())
    val pc_en           =   Input(Bool()) //for pc
    val br_j            =   Input(Bool())// whether the inst is branch or jump
    val BD              =   Output(Bool())
    val imem_notOK      =   Output(Bool()) //for risk
}



class Front extends Module{
    val io              =   IO(new pcIO())
    val pc              =   Module(new PC())

//front input
    pc.io.boot          := io.hand.boot //from AHB
    pc.io.epc       := io.epc
    pc.io.pcentry   := io.pcentry
    pc.io.gpr_rs    := io.gpr_rs
    pc.io.Imm32     := io.Imm32
    pc.io.Imm26     := io.Imm26
    pc.io.br        := io.br
    pc.io.j         := io.j
    pc.io.jr        := io.jr
    pc.io.pcjump    := io.pcjump
    pc.io.pcbranch  := io.pcbranch
    pc.io.edone     := io.edone
    pc.io.pcback    := io.pcback
    pc.io.b         := io.b
    pc.io.bimm32    := io.bimm32
    // pc.io.pc_start      := false.B

//never wirte imem
    // io.hand.if_wdata    := 0.U
    // io.hand.if_wr       := 0.U

    val pc_record          = RegInit("hbfc00000".U(32.W))
    val inst_record        = RegInit(0.U(32.W))
    val addr_record        = RegInit(0.U(32.W))//当从一个地址取址两次的时候，清除第二个取出的指令

    val first            = RegInit(1.U(1.W))
    val stall_re         = RegInit(0.U(1.W))
    val branch           = RegInit(0.U(1.W))
    val branch_next      = RegInit(0.U(1.W))//跳转类指令stall的情况需要记住的是下拍送进来的跳转之后的指令
    val stall_addrok     = RegInit(0.U(1.W))
    val W_pc_record      = pc_record

    val dmem_notok      = RegInit(0.U(1.W))
    val imem_dataok      = RegInit(0.U(1.W))

    val exception       = RegInit(0.U(1.W))
    val BD              = RegInit(0.U(1.W))//br exception
    when(io.br_j){
        BD  := 1.U
    }.otherwise{}

    when(io.epc || io.edone){
        exception    := 1.U
    }.otherwise{}

    when(io.hand.if_data_ok){
        imem_dataok         := 1.U
    }.otherwise{}
    when(io.hand.if_addr_ok){
        imem_dataok         := 0.U
    }.otherwise{}


    val realaddr = Wire(UInt(32.W))
    when(pc.io.imem_addr(31,20) === "h800".U){
        realaddr := Cat(Fill(12,0.U), pc.io.imem_addr(19,0))
    }.elsewhen(pc.io.imem_addr(31,20) === "hbfc".U){
        realaddr := Cat("h1fc".U, pc.io.imem_addr(19,0))
    }.otherwise{
        realaddr := pc.io.imem_addr //for loongson
        //realaddr := Cat("h1fc".U, pc.io.imem_addr(19,0)) //for single test
    }

    dmem_notok          := io.dmem_notok
//need if_ben=f for next inst because pc is a reg
    branch              := io.br || io.b || io.j || io.jr || io.epc || io.edone
    //branch1             := branch
//remember the stall message
    stall_re            := io.StallIF
//record the pc need to be sent to pipeline
    /**when(io.StallIF === 0.U && io.hand.if_addr_ok){
        pc_record           := pc.io.imem_addr
    }.otherwise{
        pc_record           := W_pc_record
    }*/
    pc_record           := Mux(io.StallIF === 0.U && io.hand.if_data_ok,  pc.io.imem_addr, W_pc_record)


    when (io.hand.boot){
        io.ctr.inst     :=0.U
        pc.io.epc       :=0.U
        pc.io.pcentry   :=0.U
        io.ctr.pcReg    :=0.U
    }.otherwise{
        //when((io.dmem_notok || dmem_notok === 1.U) && (io.hand.if_data_ok || imem_dataok === 1.U)){
        //when(io.dmem_notok || dmem_notok === 1.U){
            //io.hand.if_ben          := 15.U
            /**when(io.hand.if_data_ok){
                pc.io.pc_en     := true.B
            }.otherwise{
                pc.io.pc_en     := false.B
            }*/
            pc.io.pc_en     := false.B
            io.ctr.inst         := 0.U
           /** when(io.hand.if_addr_ok && stall_addrok === 0.U){
                //when stall is 0,need to send last inst from imem
                inst_record         := io.hand.if_rdata
                branch_next         := Mux(branch === 1.U, 1.U, 0.U)
                //record first addrok's inst
                stall_addrok        := 1.U
                //pc.io.pc_en         := Mux(branch === 1.U && io.hand.if_addr_ok, true.B, false.B)
            }.otherwise{}}.else*/
        when(branch === 1.U && io.hand.if_addr_ok && exception === 0.U ){
            io.ctr.inst         := 0.U
            //io.hand.if_ben      := 15.U
            pc.io.pc_en         := true.B
        }.otherwise{
            imem_dataok         := 0.U

        when(io.hand.if_addr_ok ){
            io.hand.if_addr := realaddr
            //when data is ok, pc can change
            pc.io.pc_en         := true.B
        }.otherwise{
            pc.io.pc_en         := false.B
        }
        when(io.hand.if_data_ok){
            stall_addrok    := Mux(io.StallIF === 1.U, stall_addrok, 0.U)
            first := 0.U
            exception    := 0.U
            io.BD          := BD
            BD             := 0.U

            //io.hand.if_ben      := 15.U
            //when data is ok, last pc can issue to pipeline
            io.imem_notOK       := false.B
            //inst_record         := io.hand.if_rdata

            io.ctr.inst         := Mux(stall_addrok === 1.U && branch === 0.U && branch_next === 0.U, inst_record, io.hand.if_rdata)
            io.ctr.pcReg        := pc_record
        }.otherwise{
            //io.hand.if_ben      := 0.U
            // when(stall_re === 1.U){
            //     io.imem_notOK       := false.B
            //     io.hand.if_ben      := 15.U
            // }.otherwise{
            //     io.imem_notOK   := true.B
            //     io.ctr.inst         := 0.U
            // }

        }
        }
        //当stall的时候，将取到的上一条地址送入到流水线中，若是跳转类指令，需要
        when(io.StallIF === 1.U ){
            pc.io.pc_en         := false.B
            io.ctr.inst         := 0.U
            // when(stall_re === 0.U){
            //     inst_record         := io.hand.if_rdata
            // }
            when(io.hand.if_data_ok && stall_addrok === 0.U){
                //when stall is 0,need to send last inst from imem
                inst_record         := io.hand.if_rdata
                branch_next         := Mux(branch === 1.U, 1.U, 0.U)
                //record first addrok's inst
                stall_addrok        := 1.U
                //pc.io.pc_en         := Mux(branch === 1.U && io.hand.if_addr_ok, true.B, false.B)
            }.otherwise{}

        }.otherwise{}
        when(stall_re === 1.U && io.StallIF === 0.U && io.hand.if_data_ok){
            pc.io.pc_en         := Mux(io.hand.if_addr_ok, true.B, false.B)
            // when(stall_addrok === 0.U){
            //     io.ctr.inst         := io.hand.if_rdata
            //     //io.ctr.inst         := Mux(branch_next === 1.U || branch === 1.U, io.hand.if_rdata, inst_record)
            // }.otherwise{

            //     io.ctr.inst         :=inst_record

            // }
            io.ctr.inst         := Mux(stall_addrok === 1.U && branch_next === 0.U, inst_record, io.hand.if_rdata)
		stall_addrok    := 0.U


            branch_next         := 0.U
        }.otherwise{}

        when(io.instClr || !io.hand.if_data_ok || exception === 1.U){
            io.ctr.inst     := 0.U
        }.otherwise{}

        //pc_bad need to be outside the hand
        when(pc_record(1,0) =/= 0.U && !io.instClr) {
                io.ctr.pcbad     := true.B
                io.ctr.pcReg        := pc_record
        }.otherwise{
                io.ctr.pcbad    := false.B
        }

        when(io.br || io.b || io.j || io.jr || io.epc || io.edone){
            pc.io.pc_en     := true.B
        }

        //send first if_ben=f
        // when( first === 1.U){
        //     io.hand.if_ben      := 15.U
        //     //pc.io.pc_en         := true.B
        //     io.hand.if_addr := realaddr
        // }.otherwise{}
    }
}
