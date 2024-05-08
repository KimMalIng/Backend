#!/usr/bin/env python
# coding: utf-8

# In[5]:


import json
import datetime
import random
import copy
import time


# 유전 알고리즘을 통해 입력받은 json 형식의 스케줄을 자동으로 할당해주는 클래스 Schedule
class Schedule:

    # 시간 문자열("**:**"형태)을 입력받으면 ver이 0이면 계획의 실행 시간이 list의 time_unit에 따라 몇칸을 차지하는 지 반환하고
    # ver이 1이면 시작 시간의 list index를 반환해준다.
    def make_time(self, time_str, ver):

        # 문자열을 :를 기준으로 나눠 hour과 minute으로 time의 변수에 할당
        time = time_str.split(":")
        time = datetime.time(int(time[0]), int(time[1]))
        if ver == 0:

            # 시간과 분을 실수 형태의 시간으로 만들어서 60을 시간 구분 단위인 time_unit으로 나눈 값을 곱해준다.
            time_slot = int((float(time.hour) + float(time.minute / 60)) / (self.time_unit / 60))

            return time_slot

        elif ver == 1:

            # 입력받은 시간의 hour를 (60/시간 구분 단위)만큼 곱하고 minute을 시간 구분 단위로 나눠주어 리스트에서 그 시간의 위치를 반환한다.
            time_index = int(int(time.hour) * int(60 / self.time_unit) + int(time.minute / self.time_unit))

            return time_index

    # json에 존재하는 마감 날짜와 시간을 읽어서 계획 리스트의 어느 위치인 지 튜플로 반환해주는 함수
    def make_deadline(self, deadline):
        deadline = deadline.split()

        # 마감 시간이 자정이라면 전날 즉 week_schedule의 1 작은 row의 마지막 index까지 이므로 해당 과정을 수행
        if deadline[1] == "00:00":
            # end_date
            if deadline[0] not in self.week_dic:
                row = self.week_len - 1
                column = int(24 * (60 / self.time_unit) - 1)
            else:
                row = self.week_dic[deadline[0]] - 1
                column = int(24 * (60 / self.time_unit) - 1)

        # 마감 시간이 자정이 아니라면 make_time를 통해 week_schedule의 시간 index로 바꾼다
        else:
            row = self.week_dic[deadline[0]]
            # make_time의 ver=1은 start_time의 index를 반환하기 때문에 반환값에서 1을 빼주어야 마감 시간을 구할 수 있다.
            column = int(self.make_time(deadline[1], 1) - 1)

        return (row, column)

    # json에 존재하는 deadline이 week_day의 날짜 안에 존재하는지 검사하고 초과한다면 estimated_time을 초과한 비율에 맞게 조정해주는 함수
    def check_deadline(self, deadline, estimated_time, startDate):
        start_time = self.json_str["schedule_startTime"]
        end_date = self.json_str["week_day"][self.week_len - 1]
        end_date = end_date.split(".")

        # end_date와 deadline의 크기를 비교하기 위해 datetime.datetime으로 datetime객체 생성
        end_date = datetime.datetime(int(end_date[0]), int(end_date[1]), int(end_date[2]))
        end_date = end_date + datetime.timedelta(days=1)

        # start_date에 따른 datetime객체 생성
        if startDate == None:
            start_date = self.json_str["week_day"][0]
        else:
            start_date = startDate
        start_date = start_date.split(".")
        start_date = datetime.datetime(int(start_date[0]), int(start_date[1]), int(start_date[2]),
                                       int(start_time.split(":")[0]), int(start_time.split(":")[1]))

        deadline = deadline.split()
        deadline_y = deadline[0].split(".")
        deadline_t = deadline[1].split(":")
        deadline = datetime.datetime(int(deadline_y[0]), int(deadline_y[1]), int(deadline_y[2]), int(deadline_t[0]),
                                     int(deadline_t[1]))

        # deadline이 end_date를 초과하면 -1, 그렇지 않으면 1값을 저장하는 변수
        deadline_exist = 1
        real_time = 0

        # deadline이 week_day안에 존재하지 않는 경우
        if deadline > end_date:

            # deadline이 end_date를 초과한만큼 estimated_time을 조정
            if int((deadline - start_date).seconds / 3600) >= 12:
                real_time = int((end_date - start_date).days / ((deadline - start_date).days + 1) * estimated_time)
            else:
                real_time = int((end_date - start_date).days / (deadline - start_date).days * estimated_time)
            deadline_exist = -1

        # deadline이 week_day안에 존재하는 경우
        elif deadline > start_date and deadline < end_date:
            real_time = estimated_time
            deadline_exist = 1

        elif deadline <= start_date:
            real_time = 0
            deadline_exist = -1

        return (deadline_exist, real_time)

    # json의 defaultJob을 읽어 입력된 시간만큼 self.week_schedule에 1을 채우는 함수
    def fill_default_job(self, default_dic):
        for k, v in default_dic.items():
            # key값이 "수면"일 경우
            if k == "수면":
                # 종료 시간을 datetime.datetime 객체로 바꿔 estimated_time과 차를 통해 자정 이전에 수면이 시작되는지 아닌지 확인
                end_time = v["endTime"]
                end_time = datetime.datetime(2023, 11, 16, int(end_time.split(":")[0]), int(end_time.split(":")[1]))
                mid_night = datetime.datetime(2023, 11, 16, 0, 0)
                estimated_time = v["estimatedTime"]
                estimated_time = datetime.time(int(estimated_time.split(":")[0]), int(estimated_time.split(":")[1]))
                start_time = end_time - datetime.timedelta(hours=estimated_time.hour, minutes=estimated_time.minute)
                # 수면이 자정 이전에 시작되는 경우
                if start_time < mid_night:
                    # 시작 시간부터 자정 이전까지의 시간을 문자열로 계산
                    before_mid = ""
                    before_mid += str(int((mid_night - start_time).seconds / 60) // 60) + ":"
                    before_mid += str(int((mid_night - start_time).seconds / 60) % 60)

                    # 수면 시작 시간을 start_time 변수에 문자열로 저장
                    start_time = str(start_time.hour) + ":" + str(start_time.minute)
                    self.is_afterMid = False
                    self.startSleep = start_time

                    # 수면 시작 시간에 대한 배열에서의 index를 make_time을 통해 구해서 beforeMid_idx에 저장
                    beforeMid_idx = self.make_time(start_time, 1)
                    # 자정 이전까지의 수면시간이 배열에서 몇 칸인지 make_time을 통해 구하여 beforeMid_slot에 저장
                    beforeMid_slot = self.make_time(before_mid, 0)

                    # 구한 수면시간을 배열에 추가
                    for i in range(self.week_len):
                        for j in range(beforeMid_slot):
                            self.week_schedule[i][beforeMid_idx + j] = 1

                    # 자정 이후의 남은 수면 시간을 문자열로 remain_time에 저장
                    remain_time = ""
                    remain_time += str(int((end_time - mid_night).seconds / 60 // 60)) + ":"
                    remain_time += str(int((end_time - mid_night).seconds / 60) % 60)
                    # 자정 이후 수면시간이 배열에서 몇 칸인지 make_time을 통해 구하여 afterMid_slot에 저장
                    afterMid_slot = self.make_time(remain_time, 0)

                    # 구한 수면시간을 배열에 추가
                    for i in range(self.week_len):
                        for j in range(afterMid_slot):
                            self.week_schedule[i][j] = 1

                # 수면시작시간이 자정 이후일 경우
                else:
                    # 시작 시간의 index를 make_time을 통해 구하고 배열에 추가
                    start_time = str(start_time.hour) + ":" + str(start_time.minute)
                    self.is_afterMid = True
                    self.startSleep = start_time
                    sleep_idx = self.make_time(start_time, 1)
                    sleep_slot = self.make_time(v["estimatedTime"], 0)
                    for i in range(self.week_len):
                        for j in range(sleep_slot):
                            self.week_schedule[i][sleep_idx + j] = 1

            # key값이 수면이 아닐 경우
            else:
                # 시작 시간의 index를 make_time을 통해 구하고 estimated_time만큼 배열에 추가
                start_time = v["startTime"]
                estimated_time = v["estimatedTime"]
                start_idx = self.make_time(start_time, 1)
                time_slot = self.make_time(v["estimatedTime"], 0)
                for i in range(self.week_len):
                    for j in range(time_slot):
                        self.week_schedule[i][start_idx + j] = 1

                        # 최초 Schedule 객체를 만들 때 호출되는 초기화 함수(json_file을 읽어들여 load해주고, week_dic에는 json의 Week_day 리스트를,

    # sch_dic에는 사용자가 입력한 배정 해주길 바라는 일정들의 {고유번호:(이름, 실행시간, 마감일자)}의 dictionary형태로 저장하며
    # week_schedule에는 사용자의 시간이 고정된 계획을 1로 입력한 (주차 남은 일자)x(24*60/시간 구분 단위)의 리스트가 저장된다.
    def __init__(self, json_path):
        with open(json_path, 'r', encoding='utf-8') as f:
            self.json_str = json.load(f)
        self.week_dic = {}
        self.sch_dic = {}

        # week_schedule의 행 크기를 위해 json파일에 며칠의 일자가 있는지 Week_day의 크기를 구해 week_len에 저장
        self.week_len = len(self.json_str["week_day"])

        # 시간 구분 단위(60이면 리스트의 한 칸이 1시간이고 10이면 한 칸이 10분)
        self.time_unit = 10
        self.day_unit = 24 * int(60 / self.time_unit) - 1

        # sch_dic의 배정할 일정들의 고유 번호를 저장하는 변수 count
        self.count = 1

        # (주차 남은 일자)x(24*60/시간 구분 단위)크기의 리스트를 값이 0인 ndarray로 초기화
        self.week_schedule = [[0 for i in range(24 * int(60 / self.time_unit))] for j in range(self.week_len)]

        self.final_solution = [[0 for i in range(24 * int(60 / self.time_unit))] for j in range(self.week_len)]

        # json에서 계획의 시작시간을 읽어와 그 시간 이후로 계획을 배정하기 위해 시작 시간 전의 리스트에 1을 저장
        plan_startTime = self.make_time(self.json_str["schedule_startTime"], 1)

        for i in range(plan_startTime):
            self.week_schedule[0][i] = 1

        # json의 defaultJob에서 {"이름": {"startTime": , "estimatedTime": }}의 format으로 dictionary 만듦
        default_dic = {}
        self.is_afterMid = False
        self.startSleep = ""
        default_job = self.json_str["defaultJobs"]
        for i in range(len(default_job)):
            default_dic[default_job[i]["name"]] = {}
            default_dic[default_job[i]["name"]]["startTime"] = default_job[i]["startTime"]
            default_dic[default_job[i]["name"]]["endTime"] = default_job[i]["endTime"]
            default_dic[default_job[i]["name"]]["estimatedTime"] = default_job[i]["estimatedTime"]

        # 배열에 식사 시간과 취침 시간 추가
        self.fill_default_job(default_dic)

        # week_schedul의 row값을 날짜로 접근하기 위해 json파일의 Week_day안에 날짜를 리스트의 row값에 대한 key값을 가지도록 dictionary week_dic를 만듦
        for i in range(self.week_len):
            self.week_dic[self.json_str["week_day"][i]] = i

        for i in range(len(self.json_str["schedule"])):
            schedule = self.json_str["schedule"][i]

            # json에서 읽어온 실행 시간을 week_schedule에 배정해야하는 시간 크기로 바꿔 run_time에 저장
            run_time = self.make_time(schedule["estimatedTime"], 0)

            # label이 0이면 시간이 고정된 일정이라는 뜻이므로 해당 시간을 읽어들여 make_time를 통해 week_schedule의 index로 바꾸고
            # 해당 index에 1값을 저장
            if schedule["label"] == 0:
                start_time = self.make_time(schedule["startTime"], 1)

                # week_schedule의 startTime index에 run_time만큼 1을 저장
                for j in range(run_time):
                    self.week_schedule[self.week_dic[schedule["day"]]][start_time + j] = 1

                # shouldClear값이 True인 경우에 취침 전까지 배열을 모두 1로 채움
                if schedule["shouldClear"] == True:
                    k = 0
                    idx = start_time + run_time
                    # 해당 일정이 끝난 이후의 index를 idx로 놓고 이후 취침 전까지 모든 일정을 다 1로 채움
                    while idx + k < 24 * int(60 / self.time_unit):
                        self.week_schedule[self.week_dic[schedule["day"]]][idx + k] = 1
                        k += 1

                    # 만약 수면시간이 자정 이후이고 startSleep시간이 00:00 이후일 때 startSleep에 저장된 시간을 make_time으로
                    # index를 만들어줘 startSleep_idx에 저장하고 자정부터 수면 시작시간 전까지 1로 채움
                    if self.is_afterMid == True and self.startSleep != "0:0":
                        startSleep_idx = self.make_time(self.startSleep, 1)
                        for k in range(startSleep_idx):
                            self.week_schedule[self.week_dic[schedule["day"]] + 1][k] = 1


            # label이 0이 아닌 다른 값은 배정을 해주어야 하는 일정이기 때문에 일정들의 {고유번호:(이름, 실행시간, 마감일자)}형태로
            # sch_dic에 저장
            else:
                self.count += 1
                if schedule["deadline"] != None:
                    deadline_check = self.check_deadline(schedule["deadline"], run_time, schedule["startDate"])
                    if deadline_check[0] == 1:
                        (row, column) = self.make_deadline(schedule["deadline"])
                        self.sch_dic[self.count] = (
                        schedule["name"], deadline_check[1], (row, column), schedule["startDate"])
                    else:
                        self.sch_dic[self.count] = (schedule["name"], deadline_check[1], None, schedule["startDate"])
                else:
                    self.sch_dic[self.count] = (schedule["name"], run_time, None, schedule["startDate"])

    def make_zero_dic(self):
        self.total_zero = {}
        for i in range(len(self.week_schedule)):
            self.total_zero[i] = {}
            j = 0
            zero_sum = 0
            piece_zero = []
            while j < len(self.week_schedule[i]):
                if self.week_schedule[i][j] == 0:
                    idx = 1
                    while True:
                        if j + idx < len(self.week_schedule[i]) and self.week_schedule[i][j + idx] == 0:
                            idx += 1
                        else:
                            break
                    zero_sum += idx
                    piece_zero.append([idx, [j, j + idx - 1]])
                    j += idx
                else:
                    j += 1
                    continue

            self.total_zero[i]["daily_zero"] = zero_sum
            self.total_zero[i]["piece_zero"] = piece_zero

    def assign_schedule(self):
        self.total_plan = {}
        for k, v in self.sch_dic.items():
            if v[3] == None:
                start_date = 0
            else:
                start_date = self.week_dic[v[3]]

            if v[2] == None:
                end_date = self.week_len - 1
                end_hour = self.day_unit
            else:
                end_date = v[2][0]
                end_hour = v[2][1]

            estimated_time = v[1]
            remain_time = estimated_time
            exec_period = ((end_date - start_date) * self.day_unit)
            self.total_plan[k] = []

            if exec_period == 0:
                piece_exec = estimated_time
            else:
                piece_exec = int(self.day_unit / exec_period * estimated_time)
                
            if estimated_time > int(60/self.time_unit) and piece_exec < int(60/self.time_unit):
                piece_exec = int(60/self.time_unit)

            for day in range(start_date, end_date):
                if self.total_zero[day]["daily_zero"] == 0:
                    continue
                    
                if remain_time < int(60/self.time_unit):
                    piece_exec = remain_time

                piece_zero = self.total_zero[day]["piece_zero"]
                subs_abs = []
                subs_list = []
                plus_count = 0

                for i in range(len(piece_zero)):
                    if piece_zero[i][0] - piece_exec < 0:
                        subs_list.append(999)
                        subs_abs.append(abs(piece_zero[i][0] - piece_exec))
                    else:
                        subs_list.append(piece_zero[i][0] - piece_exec)
                        subs_abs.append(piece_zero[i][0] - piece_exec)
                        plus_count += 1
                    
                min_idx = -1
                if plus_count > 0:
                    min_idx = subs_list.index(min(subs_list))
                else:
                    min_idx = subs_abs.index(min(subs_abs))
                    
                if piece_zero[min_idx][0] > piece_exec:
                    start = piece_zero[min_idx][1][0]
                    self.total_plan[k].append([day, [start, start + piece_exec - 1]])

                    piece_zero[min_idx][0] -= piece_exec
                    remain_time -= piece_exec
                    piece_zero[min_idx][1][0] += piece_exec
                    self.total_zero[day]["daily_zero"] -= piece_exec

                elif piece_zero[min_idx][0] == piece_exec:
                    start = piece_zero[min_idx][1][0]
                    self.total_plan[k].append([day, [start, start + piece_exec - 1]])

                    del piece_zero[min_idx]
                    remain_time -= piece_exec
                    self.total_zero[day]["daily_zero"] -= piece_exec

                else:
                    start = piece_zero[min_idx][1][0]
                    self.total_plan[k].append([day, [start, start + piece_zero[min_idx][0] - 1]])

                    remain_time -= piece_zero[min_idx][0]
                    self.total_zero[day]["daily_zero"] -= piece_zero[min_idx][0]
                    del piece_zero[min_idx]
                    
                if remain_time == 0:
                    break
                    
            while remain_time != 0:
                for day in range(start_date, end_date + 1):
                    if self.total_zero[day]["daily_zero"] == 0:
                        continue
                    if remain_time < int(60/self.time_unit):
                        piece_exec = remain_time
                    
                    piece_zero = self.total_zero[day]["piece_zero"]
                    subs_list = []
                    deadline_idx = -1
                    for i in range(len(piece_zero)):
                        if day == end_date and piece_zero[i][1][1] > end_hour:
                            deadline_time = end_hour - piece_zero[i][1][0]
                            deadline_idx = i
                            subs_list.append(deadline_time - remain_time)
                            break
                        else:
                            subs_list.append(piece_zero[i][0] - remain_time)
                    max_idx = subs_list.index(max(subs_list))
                    if deadline_idx == max_idx:
                        if subs_list[max_idx] > 0:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + remain_time - 1]])

                            piece_zero[max_idx][0] -= remain_time
                            piece_zero[max_idx][1][0] += remain_time
                            self.total_zero[day]["daily_zero"] -= remain_time
                            remain_time -= remain_time

                        elif subs_list[max_idx] == 0:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + remain_time - 1]])

                            piece_zero[max_idx][0] -= remain_time
                            piece_zero[max_idx][1][0] += remain_time
                            self.total_zero[day]["daily_zero"] -= remain_time
                            remain_time -= remain_time

                        else:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + deadline_time - 1]])

                            remain_time -= deadline_time
                            piece_zero[max_idx][1][0] += deadline_time
                            piece_zero[max_idx][0] -= deadline_time
                            self.total_zero[day]["daily_zero"] -= deadline_time

                    else:
                        if subs_list[max_idx] > 0:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + remain_time - 1]])

                            piece_zero[max_idx][0] -= remain_time
                            piece_zero[max_idx][1][0] += remain_time
                            self.total_zero[day]["daily_zero"] -= remain_time
                            remain_time -= remain_time

                        elif subs_list[max_idx] == 0:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + remain_time - 1]])

                            self.total_zero[day]["daily_zero"] -= remain_time
                            del piece_zero[max_idx]
                            remain_time -= remain_time

                        else:
                            start = piece_zero[max_idx][1][0]
                            self.total_plan[k].append([day, [start, start + piece_zero[max_idx][0] - 1]])

                            remain_time -= piece_zero[max_idx][0]
                            self.total_zero[day]["daily_zero"] -= piece_zero[max_idx][0]
                            del piece_zero[max_idx]
                    
                    if remain_time == 0:
                        break

    def fill_plan(self):
        for k, v in self.total_plan.items():
            for i in range(len(v)):
                for j in range(v[i][1][0], v[i][1][1] + 1):
                    self.week_schedule[v[i][0]][j] = k

    # 동적으로 배정된 계획을 json파일로 저장하는 함수
    def write_json(self, write_path):
        # json파일에 쓸 값을 저장하는 dictionary 값을 저장하는 변수
        json_dic = {}

        # 고정된 계획의 json 정보를 가지고 있을 리스트 변수 fix_schedule을 생성
        fix_schedule = []

        # 배정할 계획의 json 정보를 가지고 있을 리스트 변수 assign_shedule을 생성
        assign_schedule = []

        # json_dic에 처음 읽어들인 json파일의 Week_day값과 Schedule_startTime을 저장
        json_dic["week_day"] = self.json_str["week_day"]
        json_dic["schedule_startTime"] = self.json_str["schedule_startTime"]
        json_dic["defaultJobs"] = self.json_str["defaultJobs"]

        # 처음 읽어들인 json 파일의 Schedule에 존재하는 계획 중 고정된 일정을 가진 것들에 대해 fix_schedule에 추가해주는 과정
        for i in range(len(self.json_str["schedule"])):
            # label이 0이면 시간이 정해진 일정들이므로 fix_schedule에 추가
            if self.json_str["schedule"][i]["label"] == 0:
                fix_schedule.append(self.json_str["schedule"][i])

            # label이 0이 아니면 시간이 정해지지 않은 유동적인 일정들이기에 assign_schedule에 추가
            else:
                assign_schedule.append(self.json_str["schedule"][i])

        # json_dic의 Schedule란에 시간이 고정된 계획들의 정보를 저장하고 있는 fix_schedule값을 추가
        json_dic["schedule"] = fix_schedule

        # 만들어진 최종 계획의 값을 하나씩 읽으면 1과 0이 아닌 부분에 대해서 고정된 실행 시간을 json_dic에 기록
        for i in range(self.week_len):
            idx = 0
            while idx < int(24 * 60 / self.time_unit):

                # 해당 index의 값이 0과 1이 아닌경우 얼마나 연속된 값을 가지는 지 확인하기 위한 과정
                if self.week_schedule[i][idx] != 0 and self.week_schedule[i][idx] != 1:
                    # 해당 값을 plan_label에 저장
                    plan_label = self.week_schedule[i][idx]
                    count = 1

                    # 해당 idx+count가 열의 전체 index보다 작으면 반복
                    while count + idx < int(24 * 60 / self.time_unit):
                        # 연속된 값이 반복되면 count에 1추가
                        if self.week_schedule[i][idx + count] == plan_label:
                            count += 1

                        # 연속된 값이 나오지 않는 경우 while문 탈출
                        else:
                            break

                    # 시작 시간은 열의 index를 통해 구함
                    start_hour = int(idx // (60 / self.time_unit))
                    start_minute = int((idx % (60 / self.time_unit)) * self.time_unit)
                    start_time = str(str(start_hour).zfill(2)) + ":" + str(str(start_minute).zfill(2))

                    # 위의 반복문을 열의 끝까지 돌았을 경우 종료시간은 "24:00"
                    if idx + count == int(24 * 60 / self.time_unit):
                        end_time = "24:00"

                    # 위의 반복문을 도중에 빠져 나왔다면 종료 시간은 위의 반복문에서 구한 count를 idx에 더해서 구함
                    else:
                        end_hour = int((idx + count) // (60 / self.time_unit))
                        end_minute = int(((idx + count) % (60 / self.time_unit)) * self.time_unit)
                        end_time = str(str(end_hour).zfill(2)) + ":" + str(str(end_minute).zfill(2))

                    # idx에 count만큼 더해줌
                    idx += count

                    # assign_list의 어느 index에 위의 plan_label에 대한 Json 정보가 있는 지 구하는 과정
                    list_idx = -1
                    for j in range(len(assign_schedule)):
                        # "name"이 같다면 list_idx에 j를 저장하고 for문 탈출
                        if assign_schedule[j]["name"] == self.sch_dic[plan_label][0]:
                            list_idx = j
                            break

                    # 실행 시간을 count를 통해 구해서 run_time에 저장
                    run_hour = int(count // (60 / self.time_unit))
                    run_minute = int(count % (60 / self.time_unit)) * self.time_unit
                    run_time = str(str(run_hour).zfill(2)) + ":" + str(str(run_minute).zfill(2))

                    # assign_schedule_copy를 깊은 복사를 통해 만들어서 json 정보값을 바꿔도 assign_schedule의 값이 바뀌지 않도록 함
                    assign_schedule_copy = copy.deepcopy(assign_schedule)

                    # 시작 시간, 종료시간, 실행시간을 assign_schedule에 입력
                    assign_schedule_copy[list_idx]["startTime"] = start_time
                    assign_schedule_copy[list_idx]["endTime"] = end_time
                    assign_schedule_copy[list_idx]["estimatedTime"] = run_time

                    # 실행 일자는 현재 반복문의 row데이터에 대해서 입력
                    assign_schedule_copy[list_idx]["day"] = sch.json_str["week_day"][i]

                    # 위의 입력된 assign_schedule_copy의 값을 json_dic에 저장
                    json_dic["schedule"].append(assign_schedule_copy[list_idx])

                # 해당 값이 0이나 1인 경우 idx에 1 더함
                else:
                    idx += 1

        # json_dic에 저장된 값을 json파일로 저장
        with open(write_path, 'w', encoding='utf-8') as fw:
            json.dump(json_dic, fw, indent='\t', ensure_ascii=False)


if __name__ == "__main__":
    read_path = "/home/ubuntu/Backend/src/main/java/com/example/areyoup/timetable/service/data.json"
    write_path = "/home/ubuntu/Backend/src/main/java/com/example/areyoup/timetable/service/data.json"
    #  read_path = "data.json"
    # write_path = "data.json"
    sch = Schedule(read_path)
    sch.make_zero_dic()
    sch.assign_schedule()
    sch.fill_plan()
    sch.write_json(write_path)


# In[ ]:




