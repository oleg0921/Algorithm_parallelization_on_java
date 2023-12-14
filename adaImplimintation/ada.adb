with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;
with Ada.Task_Identification; use Ada.Task_Identification;
with Ada.Text_IO; use Ada.Text_IO;

procedure Parallel_Array_Sum is

   type Integer_Array is array (Positive range <>) of Integer;

   procedure Parallel_Sum(Array_Data : in out Integer_Array);

   task type Sum_Calculator(Index : Integer;
                            Left_Index : Integer;
                            Right_Index : Integer;
                            Result_Array : access Integer_Array;
                            Sync : access Ada.Task_Synchronization.Semaphore);
   end Sum_Calculator;

   task body Sum_Calculator is
   begin
      for I in Left_Index .. Right_Index loop
         Result_Array(I) := Result_Array(I) + Result_Array(Result_Array'First + Result_Array'Last - I);
      end loop;
      Sync.Release;
   end Sum_Calculator;

   task body Parallel_Sum is
      Array_Length : constant Positive := Array_Data'Length;
      Mid_Index : constant Positive := Array_Length / 2;
      Sync : Ada.Task_Synchronization.Semaphore := Ada.Task_Synchronization.Semaphore'(1);

      package Calculator_Array is new Ada.Containers.Bounded_Synchronized_Queue
        (Index_Type => Positive, Element_Type => Sum_Calculator);

      Calculators : Calculator_Array.Queue;

   begin
      for I in 1 .. Array_Length loop
         Put(Item => Sum_Calculator'(Index => I,
                                   Left_Index => I,
                                   Right_Index => Array_Length - I,
                                   Result_Array => Array_Data'Access,
                                   Sync => Sync),
             To => Calculators);
      end loop;

      while Calculators.Length > 1 loop
         while Calculators.Length > 0 loop
            declare
               Current_Calculator : Sum_Calculator;
            begin
               Current_Calculator := Calculator_Array.Dequeue(Calculators);
               Current_Calculator.Start;
            end;
         end loop;
         Sync.Wait;
      end loop;

      Put(Item => Array_Data(1), Width => 0);
   end Parallel_Sum;

   Data : Integer_Array := (1, 2, 3, 4, 5, 6);

begin
   Parallel_Sum(Data);
end Parallel_Array_Sum;