package Pipeline

import chisel3.iotesters.PeekPokeTester
import java.io.PrintWriter
import java.io.File
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

class PipelineTopTests(c: TopTop) extends PeekPokeTester(c) {

    //def addr_out    =  0x00004000
    val file        = new File("TestFiles")
    var inst_amount = 0
    var cycles      = 0
    var IPC         = 0
    var inst_last   = 0
    def base_addr   = 0
    var passed_num	= 0
    var no_passe_num= 0
    var test_num	= 0

    val no_passe_files = new ArrayBuffer[File]
    val no_passe_files_num = new ArrayBuffer[Int]

    def asUInt(InInt: Int) = (BigInt(InInt >>> 1) << 1) + (InInt & 1)

     // Reset the CPU
    def TopBoot() = {
        poke(c.io.test_wr, 1)
        poke(c.io.test_addr, 0)
        poke(c.io.test_inst, 0)
        // poke(c.io.test_dm_wr, 0)
        step(1)
    }

    def WriteImm (filename : File) = {

        var addr = 0
        var Inst = 0
        for (line <- Source.fromFile(filename).getLines){
           Inst = Integer.parseUnsignedInt(line, 16)
           poke(c.io.test_wr, 1)
           poke(c.io.test_addr, addr*4 + base_addr)
           poke(c.io.test_inst, asUInt(Inst))

           addr = addr + 1

           inst_amount = inst_amount + 1

           inst_last = Inst
           step(1)
        }
        poke(c.io.test_wr, 0)
    }

    def RunCpu (defcycle : Int, filename : File, test_num : Int) =
    {

        var flag = 0
        var lastflag = 0
        //for (i <- 0 until defcycle)
       // {

          //poke(c.io.test_dm_wr, 0)

           while(lastflag<2&&cycles<21000/**peek(c.io.MEM_WB_inst) != (inst_last)*/)
           {
                if(peek(c.io.MEM_WB_inst) == (inst_last)){
                    lastflag = lastflag +1
                }
                poke(c.io.test_wr, 0)

                cycles = cycles + 1//每次周期+1
                printf( "file = %s    cycles = %3d    MEM_WB_inst == %x    \n", filename, cycles, peek(c.io.MEM_WB_inst) )
                /*if( peek(c.io.MEM_WB_inst) == 0 ) {
                    printf("??\t")
                }else {
                    printf("!! ")
                }*/
                //printf( "inst_last == %x\n", inst_last )

                if( peek(c.io.MEM_WB_inst) == 0x26730001 ) {//测试通过的标志指令是26730001
                    flag = 1
                    //printf("get s3reg,flag = %d\n",flag)
                    poke(c.io.test_wr, 1)
                    poke(c.io.test_addr, 0)
                    poke(c.io.test_inst, 0)
                }



                step(1)
           }
       // }

        if ( flag == 1 ){
        	printf("the test file : %s is right!\n", filename)
        	printf("inst_amount = %3d    clycles = %3d    CPI = %.6f\n", inst_amount, cycles, (inst_amount * 1.0) /cycles )
        	passed_num = passed_num + 1//记录通过的测试点数目
        }else {
        	printf("the test file : %s is wrong!\n", filename)
        	no_passe_files.insert(no_passe_num, filename)//记录没通过的测试点文件名
        	no_passe_files_num.insert(no_passe_num, test_num)
        	no_passe_num = no_passe_num + 1//记录没通过的测试点数目
        }
    }

    def SubFile(dir: File): Iterator[File] = {
        val d = dir.listFiles.filter(_.isDirectory)
        val f = dir.listFiles.toIterator
        f ++ d.toIterator.flatMap(SubFile _)
    }

    /*以上都是定义了一些函数，下面正式开始跑指令，在一个大循环内
    每次从TestFiles文件夹读入一个指令文件，如果将助教发的91个
    测试指令都放到TestFiles里，则循环91次，也可以每次只放一个
    指令文件，单独测试该指令*/
    for( filename <- SubFile(file) )
    {
        printf("\nfilename = %s\n",filename)//输出测试文件名，filename是一个测试文件的名字或者说是地址
        TopBoot()//初始化一些信号
        inst_amount = 0//初始指令数
        WriteImm(filename)//将filename文件内的所有指令写入Imm
        cycles      = 0//初始化周期数
        test_num = test_num + 1
        printf("\ntest_num : %2d\n", test_num)//显示第几个测试文件
        step(1)
        RunCpu(20000, filename, test_num)//跑指令
    }
    printf("\n\npassed_num: %2d  no_passe_num : %2d\n", passed_num, no_passe_num)//输出通过和未通过的测试文件数
    if( no_passe_num > 0 ) {
    	printf("There are the no_passe_files:\n")
    	for(i <- 0 until no_passe_num) {//输出没通过的测试文件名
    		printf("%s in test_num %d\n", no_passe_files(i), no_passe_files_num(i))
    	}
    }
    printf("\n")
}
