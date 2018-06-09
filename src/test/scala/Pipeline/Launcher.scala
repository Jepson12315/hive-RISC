package Pipeline

import chisel3._
import chisel3.iotesters.Driver
import utils.ercesiMIPSRunner

object Launcher {
  val tests = Map(

    /**"ALU" -> { (backendName: String) =>
      Driver(() => new ALU(), backendName) {
        (c) => new ALUTests(c)
      }
    },*/
   "Top" -> { (backendName: String) =>
      Driver(() => new Top(), backendName)
      {
        (c) => new PipelineTop(c)
      }
    },
    "TopTop" -> { (backendName: String) =>
      Driver(() => new TopTop(), backendName)
      {
        (c) => new PipelineTopTests(c)
      }
    }
    //
   /** "PC" -> { (backendName: String) =>
      Driver(() => new PC(), backendName) {
        (c) => new PcTests(c)
      }
    }*/
    /**,
    "ALU11" -> { (backendName: String) =>
      Driver(() => new ALU11(), backendName) {
        (c) => new ALU11Tests(c)
      }
    },
    "Top" -> { (backendName: String) =>
      Driver(() => new Top(), backendName) {
        (c) => new TopTests(c)
      }
    }*/
  )

  def main(args: Array[String]): Unit = {
    ercesiMIPSRunner(tests, args)
  }
}
