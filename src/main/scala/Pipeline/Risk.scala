package Pipeline

import chisel3._
import chisel3.util._

class RiskIo extends Bundle()
{
    val IFEN       = Output(Bool())    // Pc registers stall
    val IDEN       = Output(Bool())    // IF/ID registers stall
    val EXECLR     = Output(Bool())  // ID/EXE registers clear
    val EXEN       = Output(Bool())
    val MEMEN      = Output(Bool())
    val IDCLR      = Output(Bool())
    val willbranch = Output(Bool())    //from id
    val ForwardAE1 = Output(Bool())
    val ForwardAE2 = Output(Bool())
    val ForwardAE3 = Output(Bool())
    val ForwardAE4 = Output(Bool())
    val ForwardAE5 = Output(Bool())

    val ForwardBE1 = Output(Bool())
    val ForwardBE2 = Output(Bool())
    val ForwardBE3 = Output(Bool())
    val ForwardBE4 = Output(Bool())
    val ForwardBE5 = Output(Bool())
    val willjump 	 = Output(Bool())
    val ready      = Input(Bool())    //from exe
    val mem_cmd    = Input(UInt(2.W))    //from id
    val div        = Input(Bool())       //from id
    val RtE        = Input(UInt(5.W))    //from id
    val RsE        = Input(UInt(5.W))     //from id
    val RdE        = Input(UInt(5.W))    //from id
    val RtD        = Input(UInt(5.W))    //from id
    val RsD        = Input(UInt(5.W))     //from id
    val RdD        = Input(UInt(5.W))    //from id
    val brM        = Input(Bool())    //from id
    val WriteregM  = Input(UInt(5.W))  //from exe
    val comp_out   = Input(Bool())    //from alu
    val WriteregW  = Input(UInt(5.W))		//destination address
    val WriteregE  = Input(UInt(5.W))
    val mem_val 		 = Input(Bool())
    val rwenM			   = Input(Bool())
    val rwenW 		   = Input(Bool())
    val rwenE 		   = Input(Bool())
    val jD 			     = Input(Bool())
    val div_busy     = Input(Bool())
    val mul_busy     = Input(Bool())
        //yuling
    val imem_notOK    =Input(Bool())
    val dmem_notOK    =Input(Bool())
    val mem_stall    =Input(Bool())

    val mem_valM     = Input(Bool())
    val mem_cmdM     = Input(UInt(2.W))



   //get a cacheready from dcache
   //val cacheready         =Input(Bool())    //from dcache
   //val req       			=Input(Bool())    //from dcache

}

class Risk extends Module(){
    val io = IO(new RiskIo ())

    val FlushD       = Wire(Bool())
    val FlushE       = Wire(Bool())
    val StallD       = Wire(Bool())
    val StallF       = Wire(Bool())
    val StallE       = Wire(Bool())
    val StallM       = Wire(Bool())


    io.ForwardAE1        := 0.U
    io.ForwardAE2        := 0.U
    io.ForwardAE3        := 0.U
    io.ForwardAE4        := 0.U
    io.ForwardAE5		     := 0.U

    io.ForwardBE1        := 0.U
    io.ForwardBE2        := 0.U
    io.ForwardBE3        := 0.U
    io.ForwardBE4        := 0.U
    io.ForwardBE5        := 0.U
    FlushD          := 0.U
    FlushE       		:= 0.U
    StallD       		:= 0.U
    StallF       		:= 0.U
    StallM          := 0.U
    StallE          := 0.U

    // io.MEMEN        := 0.U

    //for division
    when(io.div_busy||io.mul_busy)
    {
        StallF := true.B
        StallD := true.B
    }
        //for imem not ready
    //yuling
    when(io.imem_notOK)
    {
        StallF := true.B
        FlushD := true.B
    }
    when(io.dmem_notOK)
    {
        StallF := true.B
        StallD :=  true.B
        StallE := true.B
        StallM := true.B
    }

    // //stall pc 之后，应该等一拍，在这一拍中后面的?stall
    //     val stall_haha = RegInit(0.U(1.W))
    //     stall_haha     := StallF
    //     when(stall_haha === 1.U && StallF === false.B){
    //       StallD    := true.B
    //     }




    //Datarisk  for lw
    /**when( (!io.mem_cmd) && (io.mem_val)  ){
        when((io.RsD === io.RtE)||(io.RtD===io.RtE)){
        StallF := true.B
        StallD := true.B
        FlushE := true.B
        }
    }*/



    val dmem_notOK = RegInit(0.U(1.W))
    dmem_notOK    := io.dmem_notOK
    when(dmem_notOK === 1.U){
        StallF := true.B
    }

    /**when((io.mem_cmdM === 0.U) && (io.mem_valM)){
        when((io.RsD != 0.U)&&(io.RsD === io.WriteregM)&&(io.rwenM)){
        StallF := true.B
        StallD := true.B
        }
    }*/

    when((io.RsE != 0.U)&&(io.RsE === io.WriteregM)&&(io.rwenM) )         //if some instructions need the value of GPR[rs] at the EXE stage while its its just calculated out then you need ForwardAE2
    {io.ForwardAE2   := true.B}
    .elsewhen((io.RsE!=0.U)&&(io.RsE===io.WriteregW )&&(io.rwenW))         //if some instructions need the value of GPR[rs] at the EXE stage while its being written back then you need ForwardAE1
    {io.ForwardAE1   := true.B}

    when((io.RsD != 0.U)&&(io.WriteregE === io.RsD)&&(io.rwenE))		// its reckoned to be high only when it comes to JR
    {io.ForwardAE5  := true.B }
    .elsewhen((io.RsD != 0.U)&&(io.RsD === io.WriteregM)&&(io.rwenM))		//if some instructions need the value of GPR[rs] at the ID stage while its just calculated out then you need ForwardAE4
    {io.ForwardAE4  := true.B }
    .elsewhen((io.RsD != 0.U)&&(io.RsD === io.WriteregW)&&(io.rwenW))		//if some instructions need the value of GPR[rs] at the ID stage while its being written back then you need ForwardAE3
    {io.ForwardAE3  := true.B }



    when((io.RtE!=0.U)&&(io.RtE===io.WriteregM)&&(io.rwenM) )         //if some instructions need the value of GPR[Rt] at the EXE stage while its its just calculated out then you need ForwardBE2
    {io.ForwardBE2   := true.B}
    .elsewhen((io.RtE!=0.U)&&(io.RtE===io.WriteregW )&&(io.rwenW))         //if some instructions need the value of GPR[Rt] at the EXE stage while its being written back then you need ForwardBE1
    {io.ForwardBE1   := true.B}

    when((io.RtD != 0.U)&&(io.WriteregE === io.RtD)&&(io.rwenE))    // its reckoned to be high only when it comes to JR
    {io.ForwardBE5  := true.B }
    .elsewhen((io.RtD != 0.U)&&(io.RtD === io.WriteregM)&&(io.rwenM))   //if some instructions need the value of GPR[Rt] at the ID stage while its just calculated out then you need ForwardBE4
    {io.ForwardBE4  := true.B }
    .elsewhen((io.RtD != 0.U)&&(io.RtD === io.WriteregW)&&(io.rwenW))   //if some instructions need the value of GPR[Rt] at the ID stage while its being written back then you need ForwardBE3
    {io.ForwardBE3  := true.B }

    //1. HI LO to common registers
    //2. write twice respectively then read

    //Datarisk

    io.IFEN := StallF         //if IFEN=0,stall;else go on decoding;for ir register
    io.IDEN := StallD         //for if/id register
    io.EXECLR := FlushE       //if EXECLR=1,CLEAR,else do not clear;for id/exe register
    io.IDCLR := FlushD
    io.EXEN := StallE
    io.MEMEN := StallM



}
