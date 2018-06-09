package Pipeline

import chisel3._
import chisel3.util._
import scala.math._
object Instructions {
	// ARITHMETIC OPERATIONS   BaiCen Lu
	def NOP                = BitPat("b00000000000000000000000000000000")
	def ADD                = BitPat("b000000???????????????00000100000")
	def ADDI               = BitPat("b001000??????????????????????????")
	def ADDIU              = BitPat("b001001??????????????????????????")
	def ADDU               = BitPat("b000000???????????????00000100001")
	def CLO                = BitPat("b000000?????00000?????00001010001")
	def CLZ                = BitPat("b000000?????00000?????00001010000")
	def SLL                = BitPat("b00000000000???????????????000000")
	def SLLV               = BitPat("b000000???????????????00000000100")
	def SRA                = BitPat("b00000000000???????????????000011")
	def SRAV               = BitPat("b000000???????????????00000000111")
	def SRLV               = BitPat("b000000???????????????00000000110")
	def SRL                = BitPat("b00000000000???????????????000010")
    //def LA
    //def  LI
	def LUI                = BitPat("b00111100000?????????????????????")
    //def MOVE
    //def NEGU
	def SEB                = BitPat("b01111100000??????????10000100000")
	def SEH                = BitPat("b01111100000??????????11000100000")
	def SUB                = BitPat("b000000???????????????00000100010")
	def SUBU               = BitPat("b000000???????????????00000100011")
	// SHIFT AND ROTATE OPERATIONS
	/*def MOVN               = BitPat("b000000???????????????00000001011")
	def MOVZ               = BitPat("b000000???????????????00000001010")
	def SLT                = BitPat("b000000???????????????00000101010")
	def SLTI               = BitPat("b001010??????????????????????????")
	def SLTIU              = BitPat("b001011??????????????????????????")
	def SLTU               = BitPat("b000000???????????????00000101011")*/

	// LOGICAL AND BIT-FIELD OPERATION    YuanSong Qin
	def AND                = BitPat("b000000???????????????00000100100")
	def ANDI               = BitPat("b001100??????????????????????????")
	def EXT                = BitPat("b011111????????????????????000000")
	def INS                = BitPat("b011111????????????????????000100")

	def NOR                = BitPat("b000000???????????????00000100111")
	//def NOT               = BitPat("b")
	def OR                 = BitPat("b000000???????????????00000100101")
	def ORI                = BitPat("b001101??????????????????????????")
	def WSBH               = BitPat("b01111100000??????????00010100000")
	def XOR                = BitPat("b000000???????????????00000100110")
	def XORI               = BitPat("b001110??????????????????????????")
	// CON TEST AND CON MOVE               YuanSong Qin
	def MOVN               = BitPat("b000000???????????????00000001011")  // has been removed in Release 6 and has been replaced by the ‘SELNEZ’ instruction
	def MOVZ               = BitPat("b000000???????????????00000001010")  // has been removed in Release 6 and has been replaced by the ‘SELEQZ’ instruction
	def SELEQZ             = BitPat("b000000???????????????00000110101")  //be added to Release 6
	def SELNEZ             = BitPat("b000000???????????????00000110111")  //be added to Release 6
	def SLT                = BitPat("b000000???????????????00000101010")
	def SLTI               = BitPat("b001010??????????????????????????")
	def SLTIU              = BitPat("b001011??????????????????????????")
	def SLTU               = BitPat("b000000???????????????00000101011")
	// MULTIPLY AND DIVIDE OPERATIONS       Jian Cong
	def DIV	               = BitPat("b000000??????????0000000000011010")
	def DIVU	           = BitPat("b000000??????????0000000000011011")
	def MADD	           = BitPat("b011100??????????0000000000000000")
	def MADDU	           = BitPat("b011100??????????0000000000000001")
	def MSUB	           = BitPat("b011100??????????0000000000000100")
	def MSUBU	           = BitPat("b011100??????????0000000000000101")
	def MUL  	           = BitPat("b011100???????????????00000000010")
	def MULT	           = BitPat("b000000??????????0000000000011000")
	def MULTU	           = BitPat("b000000??????????0000000000011001")

	// ACCUMULATOR ACCESS OPERATIONS		zhiqian zhang
	def MFHI               = BitPat("b0000000000000000?????00000010000")
	def MFLO               = BitPat("b0000000000000000?????00000010010")
	def MTHI               = BitPat("b000000?????000000000000000010001")
	def MTLO               = BitPat("b000000?????000000000000000010011")


	// JUMPS AND BRANCHES					zhiqian zhang
	def B                  = BitPat("b0001000000000000????????????????")
	def BAL                = BitPat("b0000010000010001????????????????")
	def BEQ                = BitPat("b000100??????????????????????????")
	def BEQZ               = BitPat("b000100?????00000????????????????")
	def BGEZ               = BitPat("b000001?????00001????????????????")
	def BGEZAL             = BitPat("b000001?????10001????????????????")
	def BGTZ               = BitPat("b000111?????00000????????????????")
	def BLEZ               = BitPat("b000110?????00000????????????????")
	def BLTZ               = BitPat("b000001?????00000????????????????")
	def BLTZAL             = BitPat("b000001?????10000????????????????")
	def BNE                = BitPat("b000101??????????????????????????")
	def BNEZ               = BitPat("b000101?????00000????????????????")

	def J                  = BitPat("b000010??????????????????????????")
	def JAL                = BitPat("b000011??????????????????????????")
	def JALR               = BitPat("b000000?????00000??????????001001")
	def JR                 = BitPat("b000000?????0000000000?????001000")



	// LOAD AND STORE OPERATIONS            Jian Cong
	def LB                 = BitPat("b100000??????????????????????????")
	def LBU                = BitPat("b100100??????????????????????????")
	def LH                 = BitPat("b100001??????????????????????????")
	def LHU                = BitPat("b100101??????????????????????????")
	def LW                 = BitPat("b100011??????????????????????????")
	def LWL                = BitPat("b100010??????????????????????????")
	def LWR                = BitPat("b100110??????????????????????????")
	def SB                 = BitPat("b101000??????????????????????????")
	def SH                 = BitPat("b101001??????????????????????????")
	def SW                 = BitPat("b101011??????????????????????????")
	def SWL                = BitPat("b101010??????????????????????????")
	def SWR                = BitPat("b101110??????????????????????????")

	// ATOMIC READ-MODIFY-WRITE				zhiqian zhang
	def LL                 = BitPat("b011111??????????????????????????")
	def SC                 = BitPat("b111000??????????????????????????")

	// PREVILIGE INSTRUCTION
	def ERET               = BitPat("b01000010000000000000000000011000")
	def MFC0               = BitPat("b01000000000??????????00000000???")
	def MTC0               = BitPat("b01000000100??????????00000000???")
	// EXCPETION INSTRUCTIONS
	def BREAK              = BitPat("b000000????????????????????001101")
	def SYSCALL            = BitPat("b000000????????????????????001100")
	def TEQ                = BitPat("b000000????????????????????110100")
	def TGE                = BitPat("b000000????????????????????110000")
	def TGEU               = BitPat("b000000????????????????????110001")
	def TLT                = BitPat("b000000????????????????????110010")
	def TLTU               = BitPat("b000000????????????????????110011")
	def TNE                = BitPat("b000000????????????????????110110")
}

object ScalarConstants {
 // "val, fp_val, br, j, jal, jalr, renx1, renx2, mul, div, mem_val"
 // "renf1, renf2, renf3, rwen, wxd, amo,regdst,mul_divSign,memrl"
	//val X 		= BitPat("b?")
	val X 		= false.B
	val Y 		= true.B
	val N 		= false.B

 // s_alu1
	//val A_X   	= BitPat("b???")
	val A_X		= 0.U(3.W)
	val A_RS  	= 1.U(3.W)
	val A_SA    = 2.U(3.W)
	val A_IMM	= 3.U(3.W)


 // s_alu2
	//val B_X 	= BitPat("b???")
	val B_X		= 0.U(3.W)
	val B_ZERO 	= 0.U(3.W)
	val B_RT 	= 2.U(3.W)
	val B_IMM 	= 3.U(3.W) //Zero-extend
	val B_SE    = 4.U(3.W)

 // imm
	//val IMM_X  	= BitPat("b?")
	val IMM_X  	= 0.U(1.W)
  	val IMM_SE  = 0.U(1.W)
  	val IMM_ZE  = 1.U(1.W)

 // alu
 // todo: add alu FN_ functions
	  //val FN_X	= BitPat("b??????")
	val FN_X    = 0.U
  	val FN_ADD  = 0.U
  	val FN_SLL  = 1.U
  	val FN_SRA  = 2.U
  	val FN_NOR  = 3.U
  	val FN_XOR  = 4.U
  	val FN_SRL  = 5.U
  	val FN_OR   = 6.U
 	val FN_AND  = 7.U
 	val FN_LUI  = 8.U  // need to be added


  	val FN_SUB  = 18.U
  	val FN_SLT  = 19.U
  	val FN_SLTU = 20.U
  	val FN_BGEZ = 21.U
  	val FN_BGTZ = 22.U
  	val FN_BLEZ = 23.U
  	val FN_BLTZ = 24.U
  	val FN_BLTZAL = 25.U
  	val FN_BGEZAL = 26.U
  	val FN_BEQ  = 27.U
  	val FN_BNE  = 28.U



 // mem_cmd
	//val M_X 	= BitPat("b??")
	val M_X 	= 0.U(1.W)
	val M_RD	= 0.U(1.W)
	val M_WR	= 1.U(1.W)

 // mem_type
	//val MT_X 	= BitPat("b??")
	val MT_X	= 0.U(2.W)
	val MT_B 	= 0.U(2.W)
	val MT_H	= 1.U(2.W)
	val MT_W	= 2.U(2.W)
	val MT_DW	= 3.U(2.W)

 //wxd
    //val WXD_X   =BitPat("b??")
    val WXD_X	   = 0.U(3.W)
    val WXD_ALUout = 0.U(3.W)
    val WXD_MEM    = 1.U(3.W)
    val WXD_HI     = 2.U(3.W)
    val WXD_LO     = 3.U(3.W)
	val WXD_CP0    = 4.U(3.W)
 // cp0
	val CP0_X		= 0.U(2.W)
	val CP0_N		= 0.U(2.W)
}

import ScalarConstants._
import Instructions._

class IntCtrlSigs extends Bundle() {
  val ival        = Output(Bool())
  val fp_val      = Output(Bool())
  val br          = Output(Bool())
  val j           = Output(Bool())
  val jal         = Output(Bool())
  val jalr        = Output(Bool())
  val renx2       = Output(Bool())
  val renx1       = Output(Bool())
  val s_alu1      = Output(UInt(3.W))
  val s_alu2      = Output(UInt(3.W))
  val imm         = Output(UInt(1.W))
  val alu         = Output(UInt(5.W))
  val mul         = Output(Bool())
  val div         = Output(Bool())
  val mem_val     = Output(Bool())
  val mem_cmd     = Output(UInt(2.W))
  val mem_type    = Output(UInt(2.W))
  val renf1       = Output(Bool())
  val renf2       = Output(Bool())
  val renf3       = Output(Bool())
  val rwen        = Output(Bool())
  val wxd         = Output(UInt(3.W))
  val cp0	      = Output(UInt(2.W))
  val amo         = Output(Bool())
  val regdst      = Output(Bool())
  val mul_divSign = Output(Bool())
  val memrl       = Output(Bool())
  val mem_et 	  = Output(Bool())
  val sys   = Output(Bool())
  val bp 	= Output(Bool())
  val ri 	= Output(Bool())
  val eret  = Output(Bool())
  val b 	= Output(Bool())
  val cp0_read = Output(Bool())
  val cp0_write = Output(Bool())
}
class cpathIO extends Bundle(){
	val ctrl = new IntCtrlSigs()
	val inst = Input(UInt(32.W))
}

class Cpath extends Module
{
	val io = IO(new cpathIO());
	val csigs =
      ListLookup(io.inst,
                            List(N,X,X,X,X,X,X,X,A_X, B_X,IMM_X,FN_X,X,  X, N,M_X,MT_X,X,X,X,N,WXD_X,CP0_N,X,X,X,X),
               Array(
// ARITHMETIC OPERATIONS   BaiCen Lu
        //List(N,X,X,X,X,X,X,X,A_X, B_X,IMM_X,FN_X,X,  X, N,M_X,MT_X,X,X,X,X,X,CP0_N,X)
        ADD      -> List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_ADD, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_N,N,Y,Y,X),
        ADDI     -> List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,Y,X),
        ADDIU    -> List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        ADDU     -> List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X , FN_ADD, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        //CLO->     (Y,N,N,N,N,N,N,Y,A_RS, B_X,   IMM_X , FN_CLO, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        //CLZ->     (Y,N,N,N,N,N,N,Y,A_RS, B_X,   IMM_X , FN_CLZ, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        LUI ->     List(Y,N,N,N,N,N,N,Y,A_IMM,B_X,   IMM_X , FN_LUI, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        //SEB->     (Y,N,N,N,N,N,Y,N,A_X,  B_RT,  IMM_X , FN_SEB, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        //SEH->     (Y,N,N,N,N,N,Y,N,A_X,  B_RT,  IMM_X , FN_SEH, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        SUB ->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SUB, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,Y,X),
        SUBU ->    List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SUB, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),


// SHIFT AND ROTATE OPERATIONS  BaiCEn Lu
        //POTR->    (Y,N,N,N,N,N,Y,Y,A_SA, B_RT,  IMM_X,  FN_POTR,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        //POTRV->   (Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_POTR,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)
        SLL ->    List(Y,N,N,N,N,N,Y,Y,A_SA, B_RT,  IMM_X,  FN_SLL ,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        SLLV->    List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SLL, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        SRA->     List(Y,N,N,N,N,N,Y,Y,A_SA, B_RT,  IMM_X,  FN_SRA, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        SRAV->    List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SRA, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),			//need to be added
        SRL->     List(Y,N,N,N,N,N,Y,Y,A_SA, B_RT,  IMM_X,  FN_SRL, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        SRLV->    List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SRL, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),

// LOGICAL AND BIT-FIELD OPERATION  Yuansong Qin
        AND->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_AND, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        ANDI->    List(Y,N,N,N,N,N,N,Y,A_RS, B_IMM, IMM_ZE, FN_AND, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        //EXT->     (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X)   //不支持
        //INS->     (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X)   //不支持
        NOP->     List(Y,N,N,N,N,N,Y,N,A_X,  B_RT,  IMM_X,  FN_SLL, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),   //不支持
        NOR->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_NOR, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        OR->      List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_OR,  N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        ORI->     List(Y,N,N,N,N,N,N,Y,A_RS, B_IMM, IMM_ZE, FN_OR,  N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        //WSBH->    (Y,N,N,N,N,N,Y,N,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)   //不支持
        XOR->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_XOR, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        XORI->    List(Y,N,N,N,N,N,N,Y,A_RS, B_IMM, IMM_ZE, FN_XOR, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
// LOAD AND STORE OPERATIONS   Jian Cong
        LB ->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_B,N,N,N,Y,WXD_MEM,   CP0_X,N,N,Y,X),
        LBU->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_B,N,N,N,Y,WXD_MEM,   CP0_X,N,N,N,X),
        LH ->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_H,N,N,N,Y,WXD_MEM,   CP0_X,N,N,Y,X),
        LHU->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_H,N,N,N,Y,WXD_MEM,   CP0_X,N,N,N,X),
        LW ->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_W,N,N,N,Y,WXD_MEM,   CP0_X,N,N,X,X),
        LWL->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_W,N,N,N,Y,WXD_MEM,   CP0_X,N,N,X,Y),
        LWR->     List(Y,N,N,N,N,N,N,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_RD, MT_W,N,N,N,Y,WXD_MEM,   CP0_X,N,N,X,N),
        SB ->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_WR, MT_B,N,N,N,N,WXD_X,     CP0_X,N,X,X,X),
        SH ->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_WR, MT_H,N,N,N,N,WXD_X,     CP0_X,N,X,X,X),
        SW ->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_WR, MT_W,N,N,N,N,WXD_X,     CP0_X,N,X,X,X),
        //SWL->     (Y,N,N,N,N,N,Y,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_WR, MT_W,N,N,N,N,WXD_X,     CP0_X,N,X,X,Y),
        //SWR->     (Y,N,N,N,N,N,Y,Y,A_RS, B_SE,  IMM_SE, FN_ADD, N,N,Y,M_WR, MT_W,N,N,N,N,WXD_X,     CP0_X,N,X,X,N)
// MULTIPLY AND DIVIDE OPERATIONS Jian Cong
        DIV  ->   List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   N,Y,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,Y,X),
        DIVU ->   List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   N,Y,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,N,X),

        //BREAK->   List(N,N,N,N,N,N,N,N, A_X ,B_X ,  IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        //SYSCALL ->List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   N,Y,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,N,X),
        //MADD ->   (Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_ADD, Y,N,N,M_X,  MT_X,N,N,N,Y,WXD_X,     CP0_X,N,X,Y,X),
        //MADDU->   (Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_ADD, Y,N,N,M_X,  MT_X,N,N,N,Y,WXD_X,     CP0_X,N,X,N,X)
        //MSUB ->   (Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SUB, Y,N,N,M_X,  MT_X,N,N,N,Y,WXD_X,     CP0_X,N,X,Y,X)
        //MSUBU->   (Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SUB, Y,N,N,M_X,  MT_X,N,N,N,Y,WXD_X,     CP0_X,N,X,N,X)
        MUL  ->   List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   Y,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,Y,Y,X),

        MULT ->   List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   Y,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,Y,X),
        MULTU->   List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_X,   Y,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,N,X),

// ATOMIC READ- MODIFY-WRITE Zhiqian Zhang
        //LL->      (Y,N,N,N,N,N,Y,N,A_X, B_X,    IMM_SE, FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_N,Y,N,X,X)
        //SC->      (Y,N,N,N,N,N,Y,N,A_X, B_X,    IMM_X , FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_MEM   ,CP0_N,Y,N,X,X)

// CON TEST AND CON MOVE               YuanSong Qin
        //MOVN->    (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)  //不支持
        //MOVZ->    (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)  //不支持
        //SELEQZ->  (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)  //不支持
        //SELNEZ->  (Y,N,N,N,N,N,Y,Y,A_X,  B_X,   IMM_X,  FN_X,   N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X)  //不支持
        SLT->     List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SLT, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),
        SLTI->    List(Y,N,N,N,N,N,N,Y,A_RS, B_IMM, IMM_SE, FN_SLT, N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        SLTIU->   List(Y,N,N,N,N,N,N,Y,A_RS, B_IMM, IMM_SE, FN_SLTU,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,N,X,X),
        SLTU->    List(Y,N,N,N,N,N,Y,Y,A_RS, B_RT,  IMM_X,  FN_SLTU,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_ALUout,CP0_X,N,Y,X,X),

// ACCUMULATOR ACCESS OPERATIONS  Zhiqian Zhang
//yuling geng
        MFHI->    List(Y,N,N,N,N,N,N,N,   A_X, B_X, IMM_X,  FN_X,   N,N,N,M_RD, MT_X,N,N,N,Y,WXD_HI,   CP0_N,N,Y,X,X),
        MFLO->    List(Y,N,N,N,N,N,N,N,   A_X, B_X, IMM_X,  FN_X,   N,N,N,M_RD, MT_X,N,N,N,Y,WXD_LO,   CP0_N,N,Y,X,X),
        MTHI->    List(Y,N,N,N,N,N,N,Y,   A_X, B_X, IMM_X,  FN_X,   N,N,N,M_WR, MT_X,N,N,N,N,WXD_HI,   CP0_N,N,X,X,X),
        MTLO->    List(Y,N,N,N,N,N,N,Y,   A_X, B_X, IMM_X,  FN_X,   N,N,N,M_WR, MT_X,N,N,N,N,WXD_LO,   CP0_N,N,X,X,X),

// JUMPS AND BRANCHES Zhiqian Zhang
        B->       List(Y,N,N,N,N,N,N,N,   A_X ,B_X, IMM_SE, FN_X  , N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        //BAL->    (Y,N,Y,Y,Y,N,N,N,A_X ,B_X   , IMM_SE, FN_X  , N,N,N,M_X,  MT_X,N,N,N,Y,WXD_MEM,     CP0_N,N,X,X,X)
        BEQ->    List(Y,N,Y,N,N,N,Y,Y, A_RS,B_RT  , IMM_SE, FN_BEQ, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        //BEQZ->   (Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BEQ, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        BGEZ->   List(Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BGEZ,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_X,     CP0_N,N,X,X,X),
        BGEZAL-> List(Y,N,Y,N,Y,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BGEZAL,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_MEM,   CP0_N,N,X,X,X),
        BGTZ->   List(Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BGTZ,N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        BLEZ->   List(Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BLEZ,N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        BLTZ->   List(Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BLTZ,N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        BLTZAL-> List(Y,N,Y,N,Y,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_BLTZAL,N,N,N,M_X,  MT_X,N,N,N,Y,WXD_MEM,   CP0_N,N,X,X,X),
        BNE->    List(Y,N,Y,N,N,N,Y,Y, A_RS,B_RT  , IMM_SE, FN_BNE, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        //BNEZ->   (Y,N,Y,N,N,N,N,Y, A_RS,B_ZERO, IMM_SE, FN_SNE, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        J->      List(Y,N,N,Y,N,N,N,N, A_X,B_X,     IMM_X,  FN_X  , N,N,N,M_X , MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        JAL->    List(Y,N,N,N,Y,N,N,N, A_X,B_X,     IMM_X,  FN_X  , N,N,N,M_X , MT_X,N,N,N,Y,WXD_X,     CP0_N,N,X,X,X),
        JALR->   List(Y,N,N,N,N,Y,N,Y, A_X,B_X ,    IMM_X , FN_X  , N,N,N,M_X , MT_X,N,N,N,Y,WXD_X,     CP0_N,N,Y,X,X),
        JR->     List(Y,N,N,Y,Y,N,N,Y, A_X,B_X    , IMM_X , FN_X  , N,N,N,M_X , MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),


// PREVILIGE INSTRUCTION

// EXCPETION INSTRUCTIONS Zhiqian Zhang
        BREAK->  List(Y,N,N,N,N,N,N,N, A_X ,B_X   , IMM_X,  FN_X  , N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
        SYSCALL->List(Y,N,N,N,N,N,N,N, A_X ,B_X   , IMM_X,  FN_X  , N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X),
		MTC0 ->List(Y,N,N,N,N,N,N,N, A_X ,B_X   , IMM_X,  FN_X  , N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_X,N,X,X,X),
        MFC0 ->List(Y,N,N,N,N,N,N,N, A_X ,B_X   , IMM_X,  FN_X  , N,N,N,M_X,  MT_X,N,N,N,Y,WXD_CP0,     CP0_X,N,X,X,X),
        ERET ->List(Y,N,N,N,N,N,N,N, A_X ,B_X   , IMM_X,  FN_X  , N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        //TEQ->    (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_BEQ, N,N,N,M_X   MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        //TGE->    (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_SUB, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        //TGEU->   (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_SUB, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        //TLT->    (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_SLT, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
        //TLTU->   (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_SLTU, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,    CP0_N,N,X,X,X)
        //TNE->    (Y,N,N,N,N,N,Y,Y, A_RS,B_RT  , IMM_X,  FN_SNE, N,N,N,M_X,  MT_X,N,N,N,N,WXD_X,     CP0_N,N,X,X,X)
 ))
	val ival :: fp_val:: br :: j :: jal:: jalr:: renx2 :: renx1:: s_alu1  :: s_alu2  :: imm  :: alu  :: mul  :: div :: mem_val ::mem_cmd :: mem_type :: cs1 = csigs
	//val (ival : Bool) :: (fp_val : Bool) :: (br : Bool) :: (j : Bool) :: (jal : Bool) :: (jalr : Bool) :: (renx2 : Bool) :: (renx1 : Bool) :: cs0 = csigs
	val  renf1 :: renf2 ::renf3 :: rwen :: wxd :: cp0 ::amo :: regdst :: mul_divSign :: memrl :: Nil = cs1


	when(io.inst === LB ||io.inst=== LH ){
            io.ctrl.mem_et:= true.B
	}
	when(io.inst ===LBU||io.inst ===LHU||io.inst===SB||io.inst===SH){
            io.ctrl.mem_et:= false.B
	}
	when(io.inst === BREAK ){
		io.ctrl.bp := true.B
	}.otherwise{
		io.ctrl.bp := false.B
	}
	when(io.inst === SYSCALL){
		io.ctrl.sys := true.B
	}.otherwise{
		io.ctrl.sys := false.B
	}
	when(ival === 0.U){
		io.ctrl.ri := true.B
	}.otherwise{
		io.ctrl.ri := false.B
	}
	when(io.inst === ERET){
		io.ctrl.eret := true.B
	}.otherwise{
		io.ctrl.eret := false.B
	}
	when(io.inst === B){
		io.ctrl.b := true.B
	}.otherwise{
		io.ctrl.b := false.B
	}

	when(io.inst === MFC0){
		io.ctrl.cp0_read := true.B
	}.otherwise{
		io.ctrl.cp0_read := false.B
	}

	when(io.inst === MTC0){
		io.ctrl.cp0_write := true.B
	}.otherwise{
		io.ctrl.cp0_write := false.B
	}

	io.ctrl.ival 	:= ival
	io.ctrl.fp_val 	:= fp_val
	io.ctrl.br 		:= br
	io.ctrl.j 		:= j
	io.ctrl.jal 	:= jal
	io.ctrl.jalr 	:= jalr
	io.ctrl.renx2 	:= renx2
	io.ctrl.renx1 	:= renx1
	io.ctrl.s_alu1 	:= s_alu1
	io.ctrl.s_alu2 	:= s_alu2
	io.ctrl.imm 	:= imm
	io.ctrl.alu 	:= alu
	io.ctrl.mul 	:= mul
	io.ctrl.div 	:= div
	io.ctrl.mem_val := mem_val
	io.ctrl.mem_cmd := mem_cmd
	io.ctrl.mem_type := mem_type
	io.ctrl.renf1 	:= renf1
	io.ctrl.renf2 	:= renf2
	io.ctrl.renf3 	:= renf3
	io.ctrl.rwen	:= rwen
	io.ctrl.wxd 	:= wxd
	io.ctrl.cp0 	:= cp0
	io.ctrl.amo		:= amo
	io.ctrl.regdst 	:= regdst
	io.ctrl.mul_divSign := mul_divSign
	io.ctrl.memrl 	:= memrl

}
