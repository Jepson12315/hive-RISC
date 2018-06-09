package Pipeline
import chisel3._
import chisel3.util._

class PC_Io extends Bundle(){

    val boot            =   Input(Bool())
    val j               =   Input(Bool())
    val jr              =   Input(Bool())
    val br              =   Input(Bool())
    val b               =   Input(Bool())
    val bimm32          =   Input(UInt(32.W))
    val Imm26           =   Input(UInt(26.W))
    val Imm32           =   Input(UInt(32.W))
    val epc             =   Input(Bool())
    val pcentry         =   Input(UInt(32.W))
    val edone           =   Input(Bool())
    val pcback          =   Input(UInt(32.W))
    val gpr_rs          =   Input(UInt(32.W))
    val pcbranch        =   Input(UInt(32.W))
    val pcjump          =   Input(UInt(32.W))
    val pc_en           =   Input(Bool())

    val imem_addr       =   Output(UInt(32.W))
}
class PC extends Module{

    val pc = RegInit("hbfc00000".U(32.W))
    val W_pc = pc
    val io = IO(new PC_Io())

    //PC Module
    when(io.boot)
    {
        pc:= "hbfc00000".U
    }.otherwise{
        when(io.pc_en){
            when(io.epc){
                pc := io.pcentry
            }.elsewhen(io.edone){
                pc := io.pcback
            }.elsewhen(io.br){
                pc := io.pcbranch + 4.U + (io.Imm32<<2)
            }.elsewhen(io.b){
                pc := io.pcjump + 4.U + (io.bimm32<<2)
            }.elsewhen(io.j){
                pc := Cat(io.pcjump(31,28),io.Imm26,Fill(2,"b0".U))
            }.elsewhen(io.jr){
                pc := io.gpr_rs
            }.otherwise{
                pc := pc + 4.U
            }
        }.otherwise{
            pc     := W_pc
        }
    }
    io.imem_addr := pc
}
