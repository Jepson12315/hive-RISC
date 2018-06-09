
package Pipeline

import chisel3._
import chisel3.util._

class EXE extends Module{
	val io = IO(new Bundle{
		val in1				= Input(UInt(32.W))
		val ALU_fn 			= Input(UInt(5.W))
		val mul				= Input(Bool())
		val div				= Input(Bool())
		val mul_divsign 	= Input(Bool())
		val imm32 			= Input(UInt(32.W))
		val s_alu2			= Input(UInt(3.W))
		val busb			= Input(UInt(32.W))
		val RS       		= Input(UInt(32.W))
		val wxd 			= Input(UInt(3.W))
		val renx1           = Input(Bool())


		val ALUout			= Output(UInt(32.W))
		val ZeroException	= Output(Bool())
		val LO				= Output(UInt(32.W))
		val HI				= Output(UInt(32.W))
		val div_ready			= Output(Bool())
		val mul_ready			= Output(Bool())
		val datatomem 		= Output(UInt(32.W))
		val overflow 		= Output(Bool())
		val HI_wr			= Output(Bool())
		val LO_wr			= Output(Bool())
		val div_busy			= Output(Bool())
		val mul_busy			= Output(Bool())
		//val wxd_out 	    = Output(UInt(2.W))

	    //sramlike
	    val mem_ben          = Output(UInt(4.W))
	    val mem_wdata        = Output(UInt(32.W))
	    val mem_wr           = Output(Bool())
	    val mem_addr         = Output(UInt(32.W))
	    val mem_addr_ok      = Input(Bool())
        val mem_data_ok      = Input(Bool())

        val mem_notOK        = Output(Bool())

	    //val addrsent		 = Output(Bool())

	    val MemEn       = Input(Bool())//1表示有内存取
	    //val dat_to_mem  = Input(UInt(32.W))//写入内存的数
	    //val dmem_addr_In= Input(UInt(32.W))//写入或取内存的地址
	    val mem_cmd     = Input(UInt(2.W))
	    val mem_type    = Input(UInt(2.W))
	    val mem_et      = Input(UInt(1.W))

	    val BadVAddr    = Output(UInt(32.W))//地址错例外的错地址
	    val ExcCode     = Output(UInt(5.W))//控制寄存 Cause  ExcCode
	    val AddrExc     = Output(Bool())

	})

	val Div = Module(new div())
	val Mul = Module(new BoothMul())
	val ALU = Module(new ALU())
 	val in2 = Wire(UInt(32.W))
    io.AddrExc := false.B

 //  	val RS = RegInit(0.U(32.W))
	// RS := io.RS
    io.div_busy := Div.io.div_busy
    io.mul_busy := Mul.io.mul_busy

    val realaddr = Wire(UInt(32.W))

    when(ALU.io.ALU_out(31,20) === "h800".U){
        realaddr := Cat(Fill(12,0.U), ALU.io.ALU_out(19,0))
    }.elsewhen(ALU.io.ALU_out(31,20) === "hbfd".U){
        realaddr := Cat("h1fd".U, ALU.io.ALU_out(19,0))
    }.otherwise{
        realaddr := ALU.io.ALU_out
    }


	//connect to ALU
	io.ALUout 		:= ALU.io.ALU_out
	ALU.io.in1 		:= io.in1
	ALU.io.in2 		:= in2
	ALU.io.ALU_fn 	:= io.ALU_fn
	io.overflow 	:= ALU.io.overflow & io.mul_divsign & (~io.div) & (~io.mul)

	when(io.wxd === 2.U && io.renx1 === true.B){
		io.HI 				:= io.RS
		//io.wxd_out 			:= 0.U
		io.HI_wr			:= true.B
		io.LO_wr			:= false.B
		Div.io.rst 			:= true.B
		Mul.io.mul_rst      := true.B
		Mul.io.start		:= false.B
		Div.io.start		:= false.B
	}.elsewhen(io.wxd === 3.U && io.renx1 === true.B){
		io.LO 				:= io.RS
		//io.wxd_out 			:= 0.U
		io.LO_wr			:= true.B
		io.HI_wr			:= false.B
		Div.io.rst 			:= true.B
		Mul.io.start		:= false.B
		Div.io.start		:= false.B
		Mul.io.mul_rst      := true.B

	}.elsewhen(io.div){
		Div.io.rst 			:= false.B
		Div.io.numerator 	:= io.in1
		Div.io.denominator 	:= in2
		Div.io.ctr 			:= io.mul_divsign
		io.ZeroException 	:= Div.io.zero
		Div.io.start		:= true.B
		Mul.io.start		:= false.B
		Mul.io.mul_rst      := true.B
		io.HI_wr			:= false.B
		io.LO_wr			:= false.B
		io.div_busy         := true.B

	}.elsewhen(io.mul){
		Mul.io.start		:= true.B
 		Mul.io.multiplicand := io.in1
 		Mul.io.mulitiplier 	:= in2
 		Mul.io.is_signed 	:= io.mul_divsign
 		Div.io.rst 			:= true.B
 		Div.io.start		:= false.B
 		Mul.io.mul_rst      := false.B
		io.HI_wr			:= false.B
		io.LO_wr			:= false.B
		io.mul_busy         := true.B


 	}.otherwise{
		Div.io.rst 			:= true.B
		Mul.io.start		:= false.B
		io.HI_wr			:= false.B
		io.LO_wr			:= false.B
		Div.io.start		:= false.B
		Mul.io.mul_rst      := true.B
	}
	//io.wxd_out 			:= io.wxd
		when(Mul.io.ready){
			io.LO 				:= Mul.io.product(31,0)
			io.HI 				:= Mul.io.product(63,32)
			io.HI_wr			:= true.B
			io.LO_wr			:= true.B
		}

	io.mul_ready 				:= Mul.io.ready
	io.ZeroException 		    := Div.io.zero
	io.div_ready 				:= Div.io.ready
		when(Div.io.ready){
            when(Div.io.zero){
                io.HI_wr        := false.B
                io.LO_wr        := false.B
            }.otherwise{
                io.HI           := Div.io.remainder
                io.LO           := Div.io.quotient
                io.HI_wr        := true.B
                io.LO_wr        := true.B
            }
		}
		// .otherwise{
		// 	io.HI_wr			:= false.B
		// 	io.LO_wr			:= false.B
		// }


	when(io.s_alu2 === 2.U){
        in2  := io.busb
    }
    when(io.s_alu2 === 4.U){
        in2  := io.imm32
    }
    when(io.s_alu2 === 0.U){
        in2  := 0.U(32.W)
    }
    when(io.s_alu2 === 3.U){
        in2  := io.imm32
    }

    //io.datatomem := io.busb

  	val dat_to_mem  = Wire(UInt(32.W))//写入内存的数
  	val storen  = RegInit(0.U(1.W))
    val stopsend = RegInit(0.U(1.W))
    val addrsent = RegInit(0.U(1.W))
    dat_to_mem := io.busb

    val regaddr  = RegInit(0.U(32.W))
    val regben   = RegInit(0.U(4.W))
    val regwdata = RegInit(0.U(32.W))
    val regwr    = RegInit(0.U(1.W))
    val isbfd    = RegInit(0.U(1.W))

    val wireaddr  = Wire(UInt(32.W))
    val wireben   = Wire(UInt(4.W))
    val wirewdata = Wire(UInt(32.W))
    val wirewr    = Wire(UInt(1.W))


    when(isbfd === 1.U && io.mem_addr_ok){
        /**io.mem_ben := regben
        io.mem_wr := regwr
        io.mem_wdata := regwdata
        io.mem_addr := regaddr*/
        io.mem_ben := wireben
        io.mem_wr := wirewr
        io.mem_wdata := wirewdata
        io.mem_addr := wireaddr
    }


    when(io.mem_notOK || isbfd === 1.U){
        io.mem_ben := regben
        io.mem_wr := regwr
        io.mem_wdata := regwdata
        io.mem_addr := regaddr
    }.otherwise{
        io.mem_ben := wireben
        io.mem_wr := wirewr
        io.mem_wdata := wirewdata
        io.mem_addr := wireaddr
        when(io.MemEn){
            when(!io.mem_addr_ok){
                regben := wireben
            }

            regaddr := wireaddr
            regwdata := wirewdata
            regwr := wirewr
        }
    }


    when(io.mem_addr_ok){
        /**when(io.MemEn === 1.U){
            stopsend := 1.U
        }*/
        regben := 0.U
        when(isbfd === 0.U){
            //regben := 0.U
            regaddr := 0.U
            regwdata := 0.U
            regwr := 0.U
        }
        //addrsend := 1.U
    }.otherwise{
        //stopsend := 0.U
        //addrsent := 1.U
        //addrsend := 0.U
    }

    when(io.mem_data_ok && isbfd === 1.U){
        //regben := 0.U
        regaddr := 0.U
        regwdata := 0.U
        regwr := 0.U
        isbfd := 0.U
    }





    when(addrsent === 1.U && !io.mem_data_ok){
        io.mem_notOK := 1.U
        //gg := 22.U
    }.otherwise{
        io.mem_notOK := 0.U
        //gg := 33.U
    }

    when(addrsent === 1.U && io.mem_addr_ok){
        //stop    := 1.U
    }.otherwise{
        //stop    := 0.U
    }

    when(stopsend === 1.U){
        //stopsend := 0.U
    }

    //printf("%d",exeaddrsent)


    when(io.MemEn){

        when(io.mem_type === 2.U){//MT_W SW LW
            /** 存取字（32位）
             31       24 23       16 15        8 7         0
             ___________ ___________ ___________ ___________
            |     3     |     2     |     1     |     0     |
            */
            when(ALU.io.ALU_out(1,0)=/=0.U){
            //地址不是4的倍数 发生异常
                io.BadVAddr := ALU.io.ALU_out
                //io.ExcCode := 5.U
                when(io.mem_cmd === 1.U){//SW
                    io.ExcCode := 5.U
                }
                when(io.mem_cmd === 0.U){//LW
                    io.ExcCode := 4.U
                }
                io.AddrExc := true.B
                wireben := "b0000".U
            }.otherwise{
                when(io.mem_cmd === 1.U){//SW
                    wireben  := "b1111".U
                    addrsent := 1.U
                    wirewdata:= dat_to_mem
                    wirewr   := 1.U
                    //toren := 1.U
                    wireaddr := realaddr

                    //gg := 1.U
                    /**when(io.mem_addr_ok && !io.mem_data_ok){
                        io.mem_notOK := 1.U
                    }.otherwise{
                        io.mem_notOK := 0.U
                    }*/
                    //io.mem_notOK := 0.U
                    //gg:=222222.U

                }
                when(io.mem_cmd === 0.U){//LW
                    wireben  := "b1111".U
                    addrsent := 1.U
                    //io.mem_wdata:= dat_to_mem
                    wirewr   := 0.U
                    wireaddr := realaddr
                }/**.otherwise{
                    wireben := "b0000".U
                }*/
            }
        }
        .elsewhen(io.mem_type === 0.U){//MT_B LB LBU SB
            /** 存取字节（8位）
             7         0
             ___________
            |     0     |
            */
            when(io.mem_cmd ===1.U){//wr dm
                //io.dat_to_reg := 0.U
                //gg := 2.U
                when(io.mem_et===0.U){//SB
                    when(ALU.io.ALU_out(1,0) === "b00".U){
                        wireben  :=  "b0001".U
                        addrsent := 1.U
                        wirewdata:= Cat(Fill(24,0.U), dat_to_mem(7,0))
                    }
                    when(ALU.io.ALU_out(1,0) === "b01".U){
                        wireben  :=  "b0010".U
                        addrsent := 1.U
                        wirewdata:= Cat(Fill(16,0.U), dat_to_mem(7,0), Fill(8,0.U))
                    }
                    when(ALU.io.ALU_out(1,0) === "b10".U){
                        wireben  :=  "b0100".U
                        addrsent := 1.U
                        wirewdata:= Cat(Fill(8,0.U), dat_to_mem(7,0), Fill(16,0.U))
                    }
                    when(ALU.io.ALU_out(1,0) === "b11".U){
                        wireben  :=  "b1000".U
                        addrsent := 1.U
                        wirewdata:= Cat(dat_to_mem(7,0), Fill(24,0.U))
                    }

                    wirewr   := 1.U
                    //storen := 1.U
                    wireaddr := realaddr
                    //io.mem_notOK := 0.U
                    //gg:=333333.U
                }
            }
            when(io.mem_cmd ===0.U){//LBU&LB
                when(ALU.io.ALU_out(1,0) === "b00".U){
                    wireben  :=  "b0001".U
                    addrsent := 1.U
                }
                when(ALU.io.ALU_out(1,0) === "b01".U){
                    wireben  :=  "b0010".U
                    addrsent := 1.U
                }
                when(ALU.io.ALU_out(1,0) === "b10".U){
                    wireben  :=  "b0100".U
                    addrsent := 1.U
                }
                when(ALU.io.ALU_out(1,0) === "b11".U){
                    wireben  :=  "b1000".U
                    addrsent := 1.U
                }
                wirewr   := 0.U
                wireaddr := realaddr
                /**when(io.mem_addr_ok && !io.mem_data_ok || storen === 1.U){
                    io.mem_notOK := 1.U
                    gg:=333.U
                }.otherwise{
                    io.mem_notOK := 0.U
                }*/
            }
        }
        when(io.mem_type === 1.U){//MT_H  LH LHU SH
            /** 存取半字（16位）
             15        8 7         0
             ___________ ___________
            |     1     |     0     |
            */
            when(io.mem_cmd === 1.U){
                when(io.mem_et=== 0.U){//SH
                    when(ALU.io.ALU_out(0) =/= 0.U){
                        io.BadVAddr := ALU.io.ALU_out
                        io.ExcCode := 5.U
                        io.AddrExc := true.B
                        wireben := "b0000".U
                    }.otherwise{
                        //io.dat_to_reg   := 0.U
                        //gg := 3.U
                        when(ALU.io.ALU_out(1,0) === "b00".U){
                            wireben  :=  "b0011".U
                            addrsent := 1.U
                            wirewdata:= Cat(Fill(16,0.U), dat_to_mem(15,0))
                        }
                        when(ALU.io.ALU_out(1,0) === "b10".U){
                            wireben  :=  "b1100".U
                            addrsent := 1.U
                            wirewdata:= Cat(dat_to_mem(15,0), Fill(16,0.U))
                        }

                        wirewr   := 1.U
                        storen := 1.U
                        wireaddr := realaddr
                    }
                    //io.mem_notOK := 0.U
                    //gg:=4444444.U
                }
            }.elsewhen(io.mem_cmd === 0.U){//LHU&LH
                when(io.mem_et === 0.U){//LHU
                    when(ALU.io.ALU_out(0) =/= 0.U){
                        io.BadVAddr := ALU.io.ALU_out
                        io.ExcCode  :=4.U
                        io.AddrExc := true.B
                        wireben := "b0000".U
                    }.otherwise{
                        when(ALU.io.ALU_out(1,0) === "b00".U){
                            wireben  :=  "b0011".U
                            addrsent := 1.U
                        }
                        when(ALU.io.ALU_out(1,0) === "b10".U){
                            wireben  :=  "b1100".U
                            addrsent := 1.U
                        }
                        wirewr   := 0.U
                        wireaddr := realaddr
                    }
                }
                when(io.mem_et === 1.U){//LH
                    when(ALU.io.ALU_out(0) =/= 0.U){
                        io.BadVAddr := ALU.io.ALU_out
                        io.ExcCode  := 4.U
                        io.AddrExc := true.B
                        wireben := "b0000".U
                    }.otherwise{
                        when(ALU.io.ALU_out(1,0) === "b00".U){
                            wireben  :=  "b0011".U
                            addrsent := 1.U
                        }
                        when(ALU.io.ALU_out(1,0) === "b10".U){
                            wireben  :=  "b1100".U
                            addrsent := 1.U
                        }
                        wirewr   := 0.U
                        wireaddr := realaddr
                    }
                }.otherwise{/**wireben := "b0000".U*/}
            }.otherwise{/**wireben := "b0000".U*/}
        }.otherwise{/**wireben := "b0000".U*/}
    }.otherwise{
        wireben := "b0000".U
        //io.mem_notOK := 0.U
        wireaddr := "b00000000".U
        //io.dat_to_reg  := 0.U
        //gg := 4.U


        //无访存
    }
    when(io.mem_data_ok){
        addrsent := 0.U
    }.otherwise{}


    when(ALU.io.ALU_out(31,20) === "hbfd".U && io.MemEn){
        isbfd := true.B
        //regben := wireben
        //regaddr := wireaddr
        //regwdata := wirewdata
        //regwr := wirewr

    }.otherwise{
        //isbfd := false.B
    }

}
