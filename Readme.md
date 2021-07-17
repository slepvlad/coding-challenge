Дано:
Существует набор репозиториев с BPMN процессами, с точки зрения программы, это директория в которой находятся другие директории, а внутри них файлы.
Цель: инструмент, который позволил бы аналитикам удобно видеть взаимосвязи между процессами, обусловленные коммуникацией с помощью сигналов. На вход инструмент получает все диаграммы процессов, на выходе диаграмма показывающая взаимосвязи.
Вводные от архитектора:
Необходимо распарсить все процессы (файлы с расширением bpmn) и построить диаграмму, на которой будет видно, как процессы связаны друг с другом, посредством сигналов.
Заниматься "рисованием" диаграммы не стоит, если, конечно, нет большого желания. Мы предлагаем использовать cytoscape, но если он не вызывает энтузиазма можно воспользоваться любой библиотекой/фреймворком - всё, что угодно.

Подробности полезные при реализации:
Примеры процессов, общающихся посредством сигналов в архиве вместе с коротким описанием.
Пояснение на примере SendBIProcessingNotifications.bpmn
Из всего процесса нас интересуют только сигналы. Процесс может отправлять сигналы и ожидать сигналы и это может описываться способами, описанными в Signals examples.txt из архива.
Алгоритм может быть примерно таким: читаем все bpmn файлы, ищем места относящиеся к сигналам. Храним в памяти объекты, содержащие название процесса, название сигнала, направление сигнала (отправляет или получает)

Что интересного есть в  SendBIProcessingNotifications:
Ожидание сигналов:
<intermediateCatchEvent id="signalintermediatecatchevent1" name="SignalCatchEvent">
    <signalEventDefinition signalRef="FdlCompleted"></signalEventDefinition>
</intermediateCatchEvent>
...
<intermediateCatchEvent id="signalintermediatecatchevent2" name="SignalCatchEvent">
    <signalEventDefinition signalRef="FraudCompleted"></signalEventDefinition>
</intermediateCatchEvent>
...
<intermediateCatchEvent id="signalintermediatecatchevent3" name="SignalCatchEvent">
    <signalEventDefinition signalRef="IncomingCompleted"></signalEventDefinition>
</intermediateCatchEvent>
...
<intermediateCatchEvent id="signalintermediatecatchevent4" name="SignalCatchEvent">
    <signalEventDefinition signalRef="AcquiringCompleted"></signalEventDefinition>
</intermediateCatchEvent>
...
<boundaryEvent id="boundarysignal1" name="Signal" attachedToRef="usertask1" cancelActivity="true">
    <signalEventDefinition signalRef="AcquiringCompleted"></signalEventDefinition>
</boundaryEvent>

Получается, что этот процесс не кидает ни одного сигнала, но ожидает 4 сигнала (AcquiringCompleted в двух местах)

Значит в памяти после парсинга этого файла должно остаться:
SendBIProcessingNotifications:FdlCompleted:wait
SendBIProcessingNotifications:FraudCompleted:wait
SendBIProcessingNotifications:IncomingCompleted:wait
SendBIProcessingNotifications:AcquiringCompleted:wait

на основании одного процесса(1 bpmn файла) в результирующую схему ничего попасть не может, т.к. нас интересует взаимодействие между процессами. Нужно парсить другие файлы. Например, Common_Acquiring_EOD

    <intermediateThrowEvent id="signalintermediatethrowevent1" name="SignalThrowEvent">
      <signalEventDefinition signalRef="AcquiringCompleted"></signalEventDefinition>
    </intermediateThrowEvent>

Согласно Signals examples.txt , это отправление сигнала, значит к уже имеющимся после парсинга первого файла записям должна добавиться одна:
Common_Acquiring_EOD:AcquiringCompleted:send

Теперь можно говорить о том, что должно оказаться в результирующем файле. Получается, что мы нашли одну связку - Common_Acquiring_EOD отправляет сигнал AcquiringCompleted в  SendBIProcessingNotifications. Значит результирующая схема должна содержать два узла: Common_Acquiring_EOD, SendBIProcessingNotifications и стрелку из первого во второй, над которой написано название сигнала -  AcquiringCompleted.
