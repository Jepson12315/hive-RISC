package Pipeline

import chisel3._
import chisel3.util._

class div extends Module{
    val io = IO(new Bundle{
        val numerator = Input(UInt(32.W))//被除数
        val denominator = Input(UInt(32.W))//除数
        val rst = Input(Bool())//重置信号，1时重置（用于未做除法时）
        val ctr = Input(Bool())//计算类型选择，1是为有符号除法，0为无符号除法
        val start  = Input(Bool())//为高位时才能开始运算

        val quotient =Output(UInt(32.W))//商
        val remainder = Output(UInt(32.W))//余数
        val ready = Output(Bool())//结果状态，1时计算完毕
        val zero = Output(Bool())//1时发生除零异常
        val div_busy                = Output(Bool())
    })

    val remainder = Reg(UInt(33.W))//余数寄存器
    val quotient = Reg(UInt(33.W))//商寄存器
    val denominator = Reg(UInt(33.W))//除数寄存器
    //geng
    val in2 = Reg(UInt(32.W))//真 除数寄存器
    val in1 = Reg(UInt(32.W))//真 被除数寄存器
    val ctr = Reg(UInt(1.W))//sign 寄存器
    //val start = Reg(UInt(1.W))

    val N = Reg(UInt(6.W))
    val k = Reg(UInt(6.W))

    val FF1_1 = Module(new FF1())
    val FF1_2 = Module(new FF1())
    val sign_right_shift = Module(new sign_right_shift())

    io.zero := false.B
    io.ready := false.B

    val busy = RegInit(0.U(1.W))

    //FSM
    val s_init :: s_shift :: s_sub :: s_result :: s_no :: Nil = Enum(5)
    val state = RegInit(s_init)
    // when(io.rst === false.B ){//|| io.start === false.B){//不做除法时将除法器状态始终置为init状态
    //     state := s_no
    //     io.ready := false.B
    //     k:=0.U

    // }
    //when(start === true.B )
//{
    when(io.start){
    in2 := io.denominator
    in1 := io.numerator
    ctr := io.ctr
     busy := 1.U
     io.div_busy := true.B
     state := s_init
        io.ready := false.B
        k:=0.U

    }
    switch(state){

        is(s_init){
            io.zero := in2 === 0.U//判断除零
            //各寄存器初始化
            quotient := 0.U
            remainder := Mux(ctr === 1.U,Cat(in1(31),in1),Cat(0.U,in1))
            FF1_1.io.dat_in := Mux(ctr === 1.U&&in1(31)===1.U,~in1,in1)
            FF1_2.io.dat_in := Mux(ctr === 1.U&&in2(31)===1.U,~in2,in2)
            val Ntemp = FF1_1.io.dat_out - FF1_2.io.dat_out

            when(ctr === true.B&&in1(31)===1.U&&in2(31)===0.U){//有符号N值确定
                N := Ntemp + 1.U
            }.otherwise{//无符号除法N值确定
                N := Ntemp
            }
            //状态转移
            when(in2 =/= 0.U){
                when(ctr === 1.U&&in1(31)===1.U&&in2(31)===0.U&&FF1_1.io.dat_out + 1.U >= FF1_2.io.dat_out||FF1_1.io.dat_out >= FF1_2.io.dat_out){
                    state := s_shift
                }.otherwise{
                    state := s_result
                }
            }.otherwise{
                state := s_result
            }
        }

        is(s_shift){
            //除数寄存器初始化
            denominator := Mux(ctr === 1.U,Cat(in2(31),in2),Cat(0.U,in2)) << N
            state := s_sub
        }

        //运算
        is(s_sub){
            when(k <= N){
                when(remainder === 0.U){
                    quotient := quotient << 1.U
                }.elsewhen(remainder(32)===denominator(32)){
                    quotient := (quotient << 1.U)+1.U
                    remainder := remainder - denominator
                }.otherwise{
                    quotient := (quotient << 1.U)-1.U
                    remainder := remainder + denominator
                }
                sign_right_shift.io.dat_in := denominator
                denominator := sign_right_shift.io.dat_out
                k := k+1.U
            }.otherwise{
                state := s_result
            }
        }
        //结果处理
        is(s_result){
            //when(ctr === 1.U){
            when(((in1(31)===0.U&&in2(31)===0.U)||ctr === false.B)&&remainder(32)===1.U){
                io.quotient := quotient(31,0) - 1.U
                io.remainder := remainder(31,0) + in2
            }.elsewhen(ctr === 1.U && in1(31)===0.U&&in2(31)===1.U&&remainder(32)===1.U){
                //            }.elsewhen(in1(31)===0.U&&in2(31)===1.U&&remainder(32)===1.U){
                io.quotient := quotient(31,0) + 1.U
                io.remainder := remainder(31,0) - in2
            }.elsewhen(ctr === 1.U && in1(31)===1.U&&in2(31)===0.U&&remainder =/= 0.U && remainder(32)===0.U){
                io.quotient := quotient(31,0) + 1.U
                io.remainder := remainder(31,0) - in2
            }.elsewhen(ctr === 1.U && in1(31)===1.U&&in2(31)===1.U&&remainder =/= 0.U && remainder(32)===0.U){
                io.quotient := quotient(31,0) -1.U
                io.remainder := remainder(31,0) + in2
            }.otherwise{
                io.quotient := quotient(31,0)
                io.remainder := remainder(31,0)
            }
            io.ready := true.B
            state := s_no
        }
        is(s_no){
            io.ready := false.B
            io.quotient := 0.U
            io.remainder := 0.U
            // busy := 0.U

        }
    }
    when(io.ready){
        busy := 0.U
    }
         io.div_busy := busy
    /////////////////////////debug//////////////////////////////
    //val clk_cnt = RegInit(0.U(32.W))
    //printf("clk_cnt:%d  remainder:%d remainder(32):%d  quotient:%d denominator:%d denominator(32):%d N:%d  k:%d\n",clk_cnt,remainder,remainder(32),quotient,denominator,denominator(32),N,k)
    //clk_cnt := clk_cnt +1.U
//}
}

//优先编码器

class FF1 extends Module{
    val io = IO(new Bundle{
        val dat_in = Input(UInt(32.W))
        val dat_out = Output(UInt(6.W))
    })

    when(io.dat_in(31)===1.U){
        io.dat_out := 31.U
    }.elsewhen(io.dat_in(30)===1.U){
        io.dat_out := 30.U
    }.elsewhen(io.dat_in(29)===1.U){
        io.dat_out :=29.U
    }.elsewhen(io.dat_in(28)===1.U){
        io.dat_out := 28.U
    }.elsewhen(io.dat_in(27)===1.U){
        io.dat_out :=27.U
    }.elsewhen(io.dat_in(26)===1.U){
        io.dat_out := 26.U
    }.elsewhen(io.dat_in(25)===1.U){
        io.dat_out :=25.U
    }.elsewhen(io.dat_in(24)===1.U){
        io.dat_out := 24.U
    }.elsewhen(io.dat_in(23)===1.U){
        io.dat_out :=23.U
    }.elsewhen(io.dat_in(22)===1.U){
        io.dat_out :=22.U
    }.elsewhen(io.dat_in(21)===1.U){
        io.dat_out := 21.U
    }.elsewhen(io.dat_in(20)===1.U){
        io.dat_out :=20.U
    }.elsewhen(io.dat_in(19)===1.U){
        io.dat_out := 19.U
    }.elsewhen(io.dat_in(18)===1.U){
        io.dat_out :=18.U
    }.elsewhen(io.dat_in(17)===1.U){
        io.dat_out := 17.U
    }.elsewhen(io.dat_in(16)===1.U){
        io.dat_out :=16.U
    }.elsewhen(io.dat_in(15)===1.U){
        io.dat_out := 15.U
    }.elsewhen(io.dat_in(14)===1.U){
        io.dat_out :=14.U
    }.elsewhen(io.dat_in(13)===1.U){
        io.dat_out := 13.U
    }.elsewhen(io.dat_in(12)===1.U){
        io.dat_out :=12.U
    }.elsewhen(io.dat_in(11)===1.U){
        io.dat_out := 11.U
    }.elsewhen(io.dat_in(10)===1.U){
        io.dat_out :=10.U
    }.elsewhen(io.dat_in(9)===1.U){
        io.dat_out := 9.U
    }.elsewhen(io.dat_in(8)===1.U){
        io.dat_out :=8.U
    }.elsewhen(io.dat_in(7)===1.U){
        io.dat_out := 7.U
    }.elsewhen(io.dat_in(6)===1.U){
        io.dat_out :=6.U
    }.elsewhen(io.dat_in(5)===1.U){
        io.dat_out := 5.U
    }.elsewhen(io.dat_in(4)===1.U){
        io.dat_out :=4.U
    }.elsewhen(io.dat_in(3)===1.U){
        io.dat_out := 3.U
    }.elsewhen(io.dat_in(2)===1.U){
        io.dat_out :=2.U
    }.elsewhen(io.dat_in(1)===1.U){
        io.dat_out := 1.U
    }.otherwise{
        io.dat_out :=0.U
    }
}

//有符号右移

class sign_right_shift extends Module{
    val io = IO(new Bundle{
        val dat_in = Input(UInt(33.W))
        val dat_out = Output(UInt(33.W))
    })

    io.dat_out := Cat(io.dat_in(32),io.dat_in(32,1))
}
