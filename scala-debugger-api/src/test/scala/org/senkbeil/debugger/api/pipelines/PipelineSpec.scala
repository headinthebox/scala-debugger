package org.senkbeil.debugger.api.pipelines

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, OneInstancePerTest}

import scala.collection.GenTraversableOnce
import scala.reflect.ClassTag

class PipelineSpec extends FunSpec with Matchers with OneInstancePerTest
  with MockFactory
{
  describe("Pipeline") {
    describe("#process") {
      it("should perform the pipeline's operation on the provided data") {
        val mockOperation = mock[Operation[Int, Int]]
        val pipeline = new Pipeline(mockOperation)
        val data = Seq(1, 2, 3)

        (mockOperation.process _).expects(data).once()

        pipeline.process(data: _*)
      }

      it("should recursively call children pipelines based on the process results") {
        val mockOperation = mock[Operation[Int, Int]]
        val mockChildOperation = mock[Operation[Int, Int]]
        val pipeline = new Pipeline(mockOperation)

        val data = Seq(1, 2, 3)
        val result = Seq(7, 8, 9, 10)

        (mockOperation.process _).expects(data).returning(result).once()
        (mockChildOperation.process _).expects(result).once()

        // Add a child pipeline
        pipeline.transform(mockChildOperation)

        pipeline.process(data: _*)
      }

      it("should return the transformed data at this point in the pipeline") {
        val expected = Seq(1, 2, 3)

        val mockOperation = mock[Operation[Int, Int]]
        val pipeline = new Pipeline(mockOperation)
        val data = expected.map(_ - 1)

        (mockOperation.process _).expects(data).returning(expected).once()

        val actual = pipeline.process(data: _*)
        actual should be (expected)
      }
    }

    describe("#transform") {
      it("should create a new child pipeline using the provided operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val mockOperation = mock[Operation[Int, Int]]
        val childPipeline = pipeline.transform(mockOperation)

        childPipeline.operation should be (mockOperation)
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val mockOperation = mock[Operation[Int, Int]]
        val childPipeline = pipeline.transform(mockOperation)

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#map") {
      it("should create a new child pipeline using the map operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.map(mockFunction[Int, Int])

        childPipeline.operation shouldBe a [MapOperation[_, _]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.map(mockFunction[Int, Int])

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#flatMap") {
      it("should create a new child pipeline using the flatMap operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.flatMap(
          mockFunction[Int, GenTraversableOnce[Int]]
        )

        childPipeline.operation shouldBe a [FlatMapOperation[_, _]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.flatMap(
          mockFunction[Int, GenTraversableOnce[Int]]
        )

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#filter") {
      it("should create a new child pipeline using the filter operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.filter(mockFunction[Int, Boolean])

        childPipeline.operation shouldBe a [FilterOperation[_]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.filter(mockFunction[Int, Boolean])

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#filterNot") {
      it("should create a new child pipeline using the filterNot operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.filterNot(mockFunction[Int, Boolean])

        childPipeline.operation shouldBe a [FilterNotOperation[_]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.filterNot(mockFunction[Int, Boolean])

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#foreach") {
      it("should create a new child pipeline using the foreach operation") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        pipeline.foreach(mockFunction[Int, Unit])

        pipeline.children.head.operation shouldBe a [ForeachOperation[_]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        pipeline.foreach(mockFunction[Int, Unit])

        pipeline.children.head.operation shouldBe a [ForeachOperation[_]]
      }
    }

    describe("#unionInput") {
      it("should union with another pipeline such that processing the union processes both pipelines") {
        val pipeline1 = new Pipeline(mock[Operation[Int, Int]])
        val pipeline2 = new Pipeline(mock[Operation[Int, Int]])

        val unionPipeline = pipeline1.unionInput(pipeline2)
        val data = Seq(1, 2, 3)

        // NOTE: Checking operations of pipelines as it is easier to test
        (pipeline1.operation.process _).expects(data).once()
        (pipeline2.operation.process _).expects(data).once()

        unionPipeline.process(data: _*)
      }
    }

    describe("#unionOutput") {
      it("should union with another pipeline such that processing the either pipeline processes the union") {
        val mockOperation1 = mock[Operation[Int, Int]]
        val pipeline1 = new Pipeline(mockOperation1)

        val mockOperation2 = mock[Operation[Int, Int]]
        val pipeline2 = new Pipeline(mockOperation2)

        val unionPipeline = pipeline1.unionOutput(pipeline2)
        val data = Seq(1, 2, 3)

        // NOTE: Adding mock operation as easier to test
        val mockAfterUnionOperation = mock[Operation[Int, Int]]
        unionPipeline.transform(mockAfterUnionOperation)

        (mockOperation1.process _).expects(data).returning(data).once()
        (mockOperation2.process _).expects(data).returning(data).once()
        (mockAfterUnionOperation.process _).expects(data).twice()

        pipeline1.process(data: _*)
        pipeline2.process(data: _*)
      }
    }

    describe("#noop") {
      it("should create a new child pipeline using a no-op") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.noop()

        childPipeline.operation shouldBe a [NoOperation[_]]
      }

      it("should add the created child pipeline to the list of children") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        val childPipeline = pipeline.noop()

        pipeline.children should contain (childPipeline)
      }
    }

    describe("#children") {
      it("should be empty when the pipeline is first created") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        pipeline.children should be (empty)
      }

      it("should return the current children contained by the pipeline") {
        val pipeline = new Pipeline(mock[Operation[Int, Int]])

        pipeline.transform(mock[Operation[Int, Int]])

        pipeline.children should have length (1)
      }
    }

    describe("#newPipeline") {
      it("should create a new pipeline with a no-op based on the class type") {
        import scala.language.existentials

        def getErasure[A, B](
          operation: Operation[A, B]
        )(implicit aClassTag: ClassTag[A], bClassTag: ClassTag[B]) = {
          (aClassTag.runtimeClass, bClassTag.runtimeClass)
        }

        val operation = Pipeline.newPipeline(classOf[AnyRef]).operation

        val (inputClass, outputClass) = getErasure(operation)

        operation shouldBe a [NoOperation[_]]
        inputClass should be (classOf[AnyRef])
        outputClass should be (classOf[AnyRef])
      }
    }
  }
}