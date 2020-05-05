/*1 *//**
  2  * 
  3  *//*
  4 package com.b510.chinadate;
  5 
  6 import java.text.SimpleDateFormat;
  7 import java.util.Calendar;
  8 import java.util.Date;
  9 import java.util.GregorianCalendar;
 10 import java.util.Locale;
 11 
 12 public class ChinaDate {
 13     final private static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0,
 14             0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0,
 15             0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540,
 16             0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5,
 17             0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
 18             0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3,
 19             0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0,
 20             0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
 21             0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8,
 22             0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570,
 23             0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5,
 24             0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0,
 25             0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50,
 26             0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0,
 27             0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
 28             0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7,
 29             0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50,
 30             0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954,
 31             0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260,
 32             0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0,
 33             0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0,
 34             0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20,
 35             0x0ada0 };
 36     final private static int[] year20 = new int[] { 1, 4, 1, 2, 1, 2, 1, 1, 2,
 37             1, 2, 1 };
 38     final private static int[] year19 = new int[] { 0, 3, 0, 1, 0, 1, 0, 0, 1,
 39             0, 1, 0 };
 40     final private static int[] year2000 = new int[] { 0, 3, 1, 2, 1, 2, 1, 1,
 41             2, 1, 2, 1 };
 42     public final static String[] nStr1 = new String[] { "", "正", "二", "三", "四",
 43             "五", "六", "七", "八", "九", "十", "冬月", "腊月" };
 44     private final static String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊",
 45             "己", "庚", "辛", "壬", "癸" };
 46     private final static String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰",
 47             "巳", "午", "未", "申", "酉", "戌", "亥" };
 48     private final static String[] Animals = new String[] { "鼠", "牛", "虎", "兔",
 49             "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
 50 
 51     private final static String[] solarTerm = new String[] { "小寒", "大寒", "立春",
 52             "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
 53             "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };
 54     private final static String[] sFtv = new String[] { "0101*元旦", "0214 情人节",
 55             "0308 妇女节", "0312 植树节", "0315 消费者权益日", "0401 愚人节", "0501 劳动节",
 56             "0504 青年节", "0512 护士节", "0601 儿童节", "0701 建党节", "0801 建军节",
 57             "0808 父亲节", "0909 mzd逝世纪念", "0910 教师节", "0928 孔子诞辰", "1001*国庆节",
 58             "1006 老人节", "1024 联合国日", "1112 孙中山诞辰", "1220 澳门回归", "1225 圣诞节",
 59             "1226 mzd诞辰" };
 60     private final static String[] lFtv = new String[] { "0101*农历春节",
 61             "0115 元宵节", "0505 端午节", "0707 七夕情人节", "0815 中秋节", "0909 重阳节",
 62             "1208 腊八节", "1224 小年", "0100*除夕" };
 63 
 64     *//**
 65      * 传回农历 y年的总天数
 66      * 
 67      * @param y
 68      * @return
 69      *//*
 70     final private static int lYearDays(int y) {
 71         int i, sum = 348;
 72         for (i = 0x8000; i > 0x8; i >>= 1) {
 73             if ((lunarInfo[y - 1900] & i) != 0)
 74                 sum += 1;
 75         }
 76         return (sum + leapDays(y));
 77     }
 78 
 79     *//**
 80      * 传回农历 y年闰月的天数
 81      * 
 82      * @param y
 83      * @return
 84      *//*
 85     final private static int leapDays(int y) {
 86         if (leapMonth(y) != 0) {
 87             if ((lunarInfo[y - 1900] & 0x10000) != 0)
 88                 return 30;
 89             else
 90                 return 29;
 91         } else
 92             return 0;
 93     }
 94 
 95     *//**
 96      * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
 97      * 
 98      * @param y
 99      * @return
100      *//*
101     final private static int leapMonth(int y) {
102         return (int) (lunarInfo[y - 1900] & 0xf);
103     }
104 
105     *//**
106      * 传回农历 y年m月的总天数
107      * 
108      * @param y
109      * @param m
110      * @return
111      *//*
112     final private static int monthDays(int y, int m) {
113         if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
114             return 29;
115         else
116             return 30;
117     }
118 
119     *//**
120      * 传回农历 y年的生肖
121      * 
122      * @param y
123      * @return
124      *//*
125     final public static String AnimalsYear(int y) {
126         return Animals[(y - 4) % 12];
127     }
128 
129     *//**
130      * 传入 月日的offset 传回干支,0=甲子
131      * 
132      * @param num
133      * @return
134      *//*
135     final private static String cyclicalm(int num) {
136         return (Gan[num % 10] + Zhi[num % 12]);
137     }
138 
139     *//**
140      * 传入 offset 传回干支, 0=甲子
141      * 
142      * @param y
143      * @return
144      *//*
145     final public static String cyclical(int y) {
146         int num = y - 1900 + 36;
147         return (cyclicalm(num));
148     }
149 
150     *//**
151      * 传出农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
152      * 
153      * @param y
154      * @param m
155      * @return
156      *//*
157     final private long[] Lunar(int y, int m) {
158         long[] nongDate = new long[7];
159         int i = 0, temp = 0, leap = 0;
160         Date baseDate = new GregorianCalendar(1900 + 1900, 1, 31).getTime();
161         Date objDate = new GregorianCalendar(y + 1900, m, 1).getTime();
162         long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
163         if (y < 2000)
164             offset += year19[m - 1];
165         if (y > 2000)
166             offset += year20[m - 1];
167         if (y == 2000)
168             offset += year2000[m - 1];
169         nongDate[5] = offset + 40;
170         nongDate[4] = 14;
171         for (i = 1900; i < 2050 && offset > 0; i++) {
172             temp = lYearDays(i);
173             offset -= temp;
174             nongDate[4] += 12;
175         }
176         if (offset < 0) {
177             offset += temp;
178             i--;
179             nongDate[4] -= 12;
180         }
181         nongDate[0] = i;
182         nongDate[3] = i - 1864;
183         leap = leapMonth(i); // 闰哪个月
184         nongDate[6] = 0;
185         for (i = 1; i < 13 && offset > 0; i++) {
186             // 闰月
187             if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
188                 --i;
189                 nongDate[6] = 1;
190                 temp = leapDays((int) nongDate[0]);
191             } else {
192                 temp = monthDays((int) nongDate[0], i);
193             }
194             // 解除闰月
195             if (nongDate[6] == 1 && i == (leap + 1))
196                 nongDate[6] = 0;
197             offset -= temp;
198             if (nongDate[6] == 0)
199                 nongDate[4]++;
200         }
201         if (offset == 0 && leap > 0 && i == leap + 1) {
202             if (nongDate[6] == 1) {
203                 nongDate[6] = 0;
204             } else {
205                 nongDate[6] = 1;
206                 --i;
207                 --nongDate[4];
208             }
209         }
210         if (offset < 0) {
211             offset += temp;
212             --i;
213             --nongDate[4];
214         }
215         nongDate[1] = i;
216         nongDate[2] = offset + 1;
217         return nongDate;
218     }
219 
220     *//**
221      * 传出y年m月d日对应的农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
222      * 
223      * @param y
224      * @param m
225      * @param d
226      * @return
227      *//*
228     final public static long[] calElement(int y, int m, int d) {
229         long[] nongDate = new long[7];
230         int i = 0, temp = 0, leap = 0;
231         Date baseDate = new GregorianCalendar(0 + 1900, 0, 31).getTime();
232         Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
233         long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
234         nongDate[5] = offset + 40;
235         nongDate[4] = 14;
236         for (i = 1900; i < 2050 && offset > 0; i++) {
237             temp = lYearDays(i);
238             offset -= temp;
239             nongDate[4] += 12;
240         }
241         if (offset < 0) {
242             offset += temp;
243             i--;
244             nongDate[4] -= 12;
245         }
246         nongDate[0] = i;
247         nongDate[3] = i - 1864;
248         leap = leapMonth(i); // 闰哪个月
249         nongDate[6] = 0;
250         for (i = 1; i < 13 && offset > 0; i++) {
251             // 闰月
252             if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
253                 --i;
254                 nongDate[6] = 1;
255                 temp = leapDays((int) nongDate[0]);
256             } else {
257                 temp = monthDays((int) nongDate[0], i);
258             }
259             // 解除闰月
260             if (nongDate[6] == 1 && i == (leap + 1))
261                 nongDate[6] = 0;
262             offset -= temp;
263             if (nongDate[6] == 0)
264                 nongDate[4]++;
265         }
266         if (offset == 0 && leap > 0 && i == leap + 1) {
267             if (nongDate[6] == 1) {
268                 nongDate[6] = 0;
269             } else {
270                 nongDate[6] = 1;
271                 --i;
272                 --nongDate[4];
273             }
274         }
275         if (offset < 0) {
276             offset += temp;
277             --i;
278             --nongDate[4];
279         }
280         nongDate[1] = i;
281         nongDate[2] = offset + 1;
282         return nongDate;
283     }
284 
285     public final static String getChinaDate(int day) {
286         String a = "";
287         if (day == 10)
288             return "初十";
289         if (day == 20)
290             return "二十";
291         if (day == 30)
292             return "三十";
293         int two = (int) ((day) / 10);
294         if (two == 0)
295             a = "初";
296         if (two == 1)
297             a = "十";
298         if (two == 2)
299             a = "廿";
300         if (two == 3)
301             a = "三";
302         int one = (int) (day % 10);
303         switch (one) {
304         case 1:
305             a += "一";
306             break;
307         case 2:
308             a += "二";
309             break;
310         case 3:
311             a += "三";
312             break;
313         case 4:
314             a += "四";
315             break;
316         case 5:
317             a += "五";
318             break;
319         case 6:
320             a += "六";
321             break;
322         case 7:
323             a += "七";
324             break;
325         case 8:
326             a += "八";
327             break;
328         case 9:
329             a += "九";
330             break;
331         }
332         return a;
333     }
334 
335     public static String today() {
336         Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
337         int year = today.get(Calendar.YEAR);
338         int month = today.get(Calendar.MONTH) + 1;
339         int date = today.get(Calendar.DATE);
340         long[] l = calElement(year, month, date);
341         StringBuffer sToday = new StringBuffer();
342         try {
343             sToday.append(sdf.format(today.getTime()));
344             sToday.append(" 农历");
345             sToday.append(cyclical(year));
346             sToday.append('(');
347             sToday.append(AnimalsYear(year));
348             sToday.append(")年");
349             sToday.append(nStr1[(int) l[1]]);
350             sToday.append("月");
351             sToday.append(getChinaDate((int) (l[2])));
352             return sToday.toString();
353         } finally {
354             sToday = null;
355         }
356     }
357     
358     public static String oneDay(int year,int month,int day) {
359         Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
360         long[] l = calElement(year, month, day);
361         StringBuffer sToday = new StringBuffer();
362         try {
363             sToday.append(sdf.format(today.getTime()));
364             sToday.append(" 农历");
365             sToday.append(cyclical(year));
366             sToday.append('(');
367             sToday.append(AnimalsYear(year));
368             sToday.append(")年");
369             sToday.append(nStr1[(int) l[1]]);
370             sToday.append("月");
371             sToday.append(getChinaDate((int) (l[2])));
372             return sToday.toString();
373         } finally {
374             sToday = null;
375         }
376     }
377 
378     private static SimpleDateFormat sdf = new SimpleDateFormat(
379             "yyyy年M月d日 EEEEE");
380 
381     *//**
382      * 农历日历工具使用演示
383      * 
384      * @param args
385      *//*
386     public static void main(String[] args) {
387         System.out.println(today());
388         System.out.println(oneDay(1989, 9, 10));
389     }
390 }*/