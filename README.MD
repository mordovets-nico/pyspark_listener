### Spark Listener for rowcount
Simple implementation of [SparkListener](https://spark.apache.org/docs/1.6.1/api/java/org/apache/spark/scheduler/SparkListener.html) interface

#### Usage example
```
sbt package 
cd target/scala-2.12
pyspark  --conf spark.driver.memory=2G --conf spark.extraListeners=com.github.mordovets_nico.PythonNotifyListener --jars pyspark_listener_2.12-1.0.0.jar 
```


```
class PythonListener(object):
	package = "com.github.mordovets_nico"
	@staticmethod
	def get_manager(self):
		jvm = self.sc._jvm
		manager = getattr(jvm, "{}.{}".format(PythonListener.package, "Manager"))
		return manager
	def __init__(self, sc):
		self.uuid = None
		self.sc = sc
		self.accumulator =  sc.accumulator(0)
	def notify(self, obj):
		import json
		"""
			This method is required by Scala Listener interface
		    we defined above.
		"""
		json_obj = json.loads(obj)
		self.accumulator.add(int(json_obj["recordsWritten"]))
	def register(self):
		manager = PythonListener.get_manager(self)
		self.uuid = manager.register(self)
		return self.uuid
	def unregister(self):
		manager =  PythonListener.get_manager(self)
		manager.unregister(self.uuid)
		self.uuid = None
	def reset_accumulator(self):
		delta = self.accumulator.value
		self.accumulator.add(-delta)
	def get_accumulator_results(self):
		return self.accumulator.value
	class Java:
		implements = ["com.github.mordovets_nico.Listener"]


from pyspark.java_gateway import ensure_callback_server_started
ensure_callback_server_started(spark._sc._gateway)
sc = spark._sc
listener = PythonListener(sc)
listener.register()
df = spark.range(10)
df.write.mode("overwrite").parquet("/tmp/range")
listener.get_accumulator_results()
listener.reset_accumulator()
```
