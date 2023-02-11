package cn.locusc.java8action.chapter12LocalDate;

import java.time.*;
import java.time.chrono.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.*;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Jay
 *
 * 2023/2/8
 */
public class ActionClass {

    private static final LocalDate date = LocalDate.of(2014, 3, 18);

    private static final LocalTime time = LocalTime.of(13, 45, 20);

    public static void main(String[] args) {
        // useLocal();
        // useTemporalField();
        // useLocalTime();
        // useParse();
        // useCompose();
        // useInstant();

        usePeriod();

        TemporalAdjuster temporalAdjuster = (Temporal t) -> {
            // 读取当前日期
            DayOfWeek dow = DayOfWeek.of(t.get(ChronoField.DAY_OF_WEEK));
            // 正常情况, 增加1天
            int dayToAdd = 1;
            // 如果当天是周五, 增加3天
            if (dow == DayOfWeek.FRIDAY) {
                dayToAdd = 3;
            } else if (dow == DayOfWeek.SUNDAY) {
                dayToAdd = 2; // 如果当天是周六, 增加2天
            }
            // 增加恰当的天数后, 返回修改的日期
            return t.plus(dayToAdd, ChronoUnit.DAYS);
        };

        // anther method
        // TemporalAdjusters.ofDateAdjuster(temporal -> { ... });

        // date.with(temporalAdjuster);
    }

    private static void useLocal() {
        // 2014-03-18
        System.out.println(date.toString());

        // 2014
        int year = date.getYear();
        System.out.println(year);

        // MARCH
        Month month = date.getMonth();
        System.out.println(month);

        // 18
        int dayOfMonth = date.getDayOfMonth();
        System.out.println(dayOfMonth);

        // TUESDAY
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        System.out.println(dayOfWeek);

        // 31 (days in March)
        int len = date.lengthOfMonth();
        System.out.println(len);

        // false (not a leap year)
        boolean leapYear = date.isLeapYear();
        System.out.println(leapYear);

        // 当前日期
        LocalDate now = LocalDate.now();
        System.out.println(now);
    }

    /**
     * TemporalField是一个接口
     * 它定义了如何访问temporal对象某个字段的值
     * ChronoField枚举实现了这一接口, 所以你可以很方便地使用get方法得到枚举元素的值
     */
    private static void useTemporalField() {
        // 2014
        long year = date.getLong(ChronoField.YEAR);
        System.out.println(year);

        // 3
        int month = date.get(ChronoField.MONTH_OF_YEAR);
        System.out.println(month);

        // 77
        int day = date.get(ChronoField.DAY_OF_YEAR);
        System.out.println(day);
    }

    private static void useLocalTime() {
        // 13
        int hour = time.getHour();
        System.out.println(hour);

        // 45
        int minute = time.getMinute();
        System.out.println(minute);

        // 20
        int second = time.getSecond();
        System.out.println(second);
    }

    private static void useParse() {
        LocalDate date = LocalDate.parse("2014-03-18");
        System.out.println(date);

        LocalTime time = LocalTime.parse("13:45:20");
        System.out.println(time);
    }

    private static void useCompose() {
        // 2014-03-18T13:45:20
        LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
        System.out.println(dt1);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        System.out.println(dt2);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        System.out.println(dt3);
        LocalDateTime dt4 = date.atTime(time);
        System.out.println(dt4);
        LocalDateTime dt5 = time.atDate(date);
        System.out.println(dt5);

        LocalDate date1 = dt1.toLocalDate();
        System.out.println(date1);
        LocalTime time1 = dt1.toLocalTime();
        System.out.println(time1);
    }

    /**
     * 机器时间
     */
    private static void useInstant() {
        Instant instant1 = Instant.ofEpochSecond(3);
        System.out.println(instant1);
        Instant instant2 = Instant.ofEpochSecond(3, 0);
        System.out.println(instant2);
        // 两秒之后再加上100w纳秒(一秒)
        Instant instant3 = Instant.ofEpochSecond(2, 1_000_000_000);
        System.out.println(instant3);
        // 四秒之前100w纳秒(一秒)
        Instant instant4 = Instant.ofEpochSecond(4, -1_000_000_000);
        System.out.println(instant4);

        // Instant的设计初衷是为了便于机器使用. 它包含的是由秒及纳秒所构成的数字.
        // 所以它无法处理那些我们非常容易理解的时间单位
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field:DayOfMonth
        int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
        System.out.println(day);
    }

    /**
     * 由于LocalDateTime和Instant是为不同的目的而设计的,一个是为了便于人阅读使用,
     * 另一个是为了便于机器处理,所以你不能将二者混用.如果你试图在这两类对象之间创建
     * duration,会触发一个DateTimeException异常.此外,由于Duration类主要用于以秒和纳
     * 秒衡量时间的长短,你不能仅向between方法传递一个LocalDate对象做参数
     */
    private static void useDuration() {
        // Duration d1 = Duration.between(time1, time2);
        // Duration d1 = Duration.between(dateTime1, dateTime2);
        // Duration d2 = Duration.between(instant1, instant2);

        Duration duration1 = Duration.ofMinutes(3);
        Duration duration2 = Duration.of(3, ChronoUnit.MINUTES);
    }

    /**
     * 如果你需要以年.月或者日的方式对多个时间单位建模,可以使用Period类. 使用该类的
     * 工厂方法between,你可以使用得到两个LocalDate之间的时长
     */
    private static void usePeriod() {
        Period tenDays = Period.between(
                LocalDate.of(2014, 3, 8),
                LocalDate.of(2014, 3, 18)
        );
        System.out.println(tenDays);

        Period tenDay = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
    }

    /**
     * with针对修改, get针对获取
     */
    private static void manipulationTime() {
        // with会在时间对象上面创建一个新的时间对象、
        // 对原时间对象来说依然保持不变

        // 2014-03-18
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        // 2011-03-18
        LocalDate date2 = date1.withYear(2011);
        // 2011-03-25
        LocalDate date3 = date2.withDayOfMonth(25);
        // 2011-09-25
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);
    }

    /**
     * 修改日期, 返回的是一个新的对象
     */
    private static void changeLocalDate() {
        LocalDate date1 = LocalDate.of(2014, 3, 18);

        // 2014-03-25
        LocalDate date2 = date1.plusWeeks(1);
        // 2011-03-25
        LocalDate date3 = date2.minusYears(3);
        // 2011-09-25
        LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);
    }

    /**
     * 比如, 将日期调整到下个周日.下个工作日, 或者是本月的最后一天
     * 可以使用重载版本的with方法, 向其传递一个提供了更多定制化选择的TemporalAdjuster对象, 更加灵活地处理日期
     */
    private static void useTemporalAdjuster() {
        LocalDate date1 = LocalDate.of(2014, 3, 18);

        // 2014-03-23
        LocalDate date2 = date1.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        // 2014-03-31
        LocalDate with = date2.with(TemporalAdjusters.lastDayOfMonth());
    }

    private static void useFormat() {
        LocalDate date = LocalDate.of(2014, 3, 18);

        String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        LocalDate date1 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date2 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);
        LocalDate date3 = LocalDate.parse(formattedDate, formatter);

        // 创建一个本地化的DateTimeFormatter
        DateTimeFormatter italianFormatter =
                DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date4 = LocalDate.of(2014, 3, 18);
        // 18. marzo 2014
        String formattedDate1 = date.format(italianFormatter);
        LocalDate date5 = LocalDate.parse(formattedDate, italianFormatter);

        // 构造一个DateTimeFormatter
        DateTimeFormatter italianFormatter1 = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);
    }

    private void useZoneId() {
        // 地区ID都为"{区域}/{城市}"的格式，这些地区集合的设定都由英特网编号分配机构(IANA)的时区数据库提供
        ZoneId romeZone = ZoneId.of("Europe/Rome");

        // toZoneId将一个老的时区对象转换为ZoneId
        ZoneId zoneId = TimeZone.getDefault().toZoneId();

        // 为时间点添加时区信息
        LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);
        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);

        // 通过ZoneId, 你还可以将LocalDateTime转换为Instant:
        // Page279 代码对不上 need -> ZoneOffset provide -> ZoneId
        // Instant instantFromDateTime = dateTime.toInstant(romeZone);

        // 可以通过反向的方式得到LocalDateTime对象:
        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);
    }

    /**
     * 另一种比较通用的表达时区的方式是利用当前时区和UTC/格林尼治的固定偏差
     */
    private void useZoneOffset() {
        // 纽约落后于伦敦5小时
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");

        // "-5:00"的偏差实际上对应的是美国东部标准时间. 注意,使用这种方式定义的ZoneOffset
        // 并未考虑任何日光时的影响, 所以在大多数情况下, 不推荐使用. 由于ZoneOffset也是ZoneId,
        // 所以你可以像代码清单12-13那样使用它. 你甚至还可以创建这样的OffsetDateTime, 它使用
        // ISO-8601的历法系统, 以相对于UTC/格林尼治时间的偏差方式表示日期时间
        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(dateTime, newYorkOffset);
    }

    /**
     * 使用别的日历系统
     * 日期及时间API的设计者建议我们使用LocalDate, 尽量避免使用ChronoLocalDate, 原
     * 因是开发者在他们的代码中可能会做一些假设, 而这些假设在不同的日历系统中, 有可能不成立.
     * 比如, 有人可能会做这样的假设, 即一个月天数不会超过31天, 一年包括12个月, 或者一年中包
     * 含的月份数目是固定的. 由于这些原因, 我们建议你尽量在你的应用中使用LocalDate, 包括存
     * 储、操作、业务规则的解读: 不过如果你需要将程序的输入或者输出本地化, 这时你应该使用ChronoLocalDate类
     */
    private void useOtherCalendarSystem() {
        LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
        JapaneseDate japaneseDate = JapaneseDate.from(date);

        // 为某个Locale显式地创建日历系统,接着创建该Locale对应的日期的实例.
        // 新的日期和时间API中, Chronology接口建模了一个日历系统, 使用它的静态工厂方法
        // ofLocale, 可以得到它的一个实例
        Chronology japaneseChronology = Chronology.ofLocale(Locale.JAPAN);
        ChronoLocalDate now = japaneseChronology.dateNow();

        // 在ISO日历中计算当前伊斯兰年中斋月的起始和终止日期
        // 取得当前的Hijrah日期, 紧接着对其进行修正, 得到斋月的第一天, 即第9个月
        HijrahDate ramadanDate =
                HijrahDate.now().with(ChronoField.DAY_OF_MONTH, 1)
                        .with(ChronoField.MONTH_OF_YEAR, 9);
        System.out.println("Ramadan starts on " +
                // IsoChronology.INSTANCE是IsoChronology类的一个静态实例
                IsoChronology.INSTANCE.date(ramadanDate) +
                " and ends on " +
                // 斋月始于2014-06-28,止于2014-07-27
                IsoChronology.INSTANCE.date(
                        ramadanDate.with(
                                TemporalAdjusters.lastDayOfMonth())));
    }

}
