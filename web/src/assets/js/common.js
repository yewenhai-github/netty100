function completion(val, len) {
    len = len || 2;
    val = val + "";
    for(; val.length < len; ) {
        val = "0" + val;
    }
    return val;
}
let fmtTimeReg = /d{1,2}|M{1,2}|yy(?:yy)?|S{1,3}|([HhMsDm])\1?/g;
let learYearMonth = [1, 3, 5, 7, 8, 10, 12];
let fmtDateFlags = {
    D: function(date) {
        return date.getDy() + "";
    },
    DD: function(date) {
        return completion(date.getDay())
    },
    d: function(date) {
        return date.getDate() + "";
    },
    dd: function(date) {
        return completion(date.getDate())
    },
    M: function(date) {
        return (date.getMonth() + 1) + "";
    },
    MM: function(date) {
        return completion(date.getMonth() + 1)
    },
    yy: function(date) {
        return completion(date.getFullYear().substr(2))
    },
    yyyy: function(date) {
        return completion(date.getFullYear())
    },
    m: function(date) {
        return completion(date.getMinutes())
    },
    mm: function(date) {
        return completion(date.getMinutes())
    },
    H: function(date) {
        return date.getHours() + "";
    },
    HH: function(date) {
        return completion(date.getHours())
    },
    h: function(date) {
        return date.getHours() % 12 || 12
    },
    hh: function(date) {
        return completion(date.getHours() % 12 || 12)
    },
    S: function(date) {
        return date.getMilliseconds() + ""
    },
    SSS: function(date) {
        return completion(date.getMilliseconds(), 3)
    },
    s: function(date) {
        return date.getSeconds() + ""
    },
    ss: function(date) {
        return completion(date.getSeconds())
    },
    season: function(date) {
        let month = date.getMonth() + 1;
        return completion(Math.ceil(month / 3))
    }
}

Idate.prototype = {
    // 获取毫秒数
    getTime() {
        return this.date.getTime()
    },

    // 获取秒
    getSeconds() {
        return fmtDateFlags.ss(this.date)
    },

    // 获取分钟
    getMinutes() {
        return fmtDateFlags.mm(this.date)
    },

    // 获取小时
    getHours(type) {
        if(type == 12) return fmtDateFlags.hh(this.date);
        else return fmtDateFlags.HH(this.date);
    },

    // 获取日
    getDate() {
        return fmtDateFlags.dd(this.date);
    },

    // 获取周几
    getWeek() {
        return fmtDateFlags.DD(this.date)
    },

    // 获取月
    getMonth() {
        return fmtDateFlags.MM(this.date)
    },

    // 获取季度
    getSeason() {
        return fmtDateFlags.season(this.date)
    },

    // 获取年
    getYear() {
        return fmtDateFlags.yyyy(this.date)
    },

    // 判断闰年
    isLeapYear(year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    },

    // 获取当前计算机时间
    now(format) {
        return new Idate(new Date(), format || this.format).getFmtTime()
    },

    // 获取格式化之后的时间
    getFmtTime(format) {
        let fmt = format || this.format;
        return fmt.replace(fmtTimeReg, (curr) => {
            return fmtDateFlags[curr](this.date)
        })
    }
}

export function Idate(date, format) {
    // if(Itools.prototype.getType(date) !== "Date" || isNaN(date.getTime())) {
    //     throw new Error("not type")
    // }
    this._date = date || "";
    this.format = format || "yyyy-MM-dd hh:mm:ss";
    this.date = new Date(this._date)
}


// 防抖
export const debounce = (callback, wait, state) =>  {
    /*
        state
            true: 立即执行
            false: 不立即执行
    */
   state = state || false;
   let timer = null;
   return function() {
       let args = arguments;
       if(timer) clearTimeout(timer);
       if(state) {
           let now = !timer;
           timer = setTimeout(() => {
               timer = null
           }, wait)
           if(now) callback.apply(this, args);
       } else {
           timer = setTimeout(() => {
               callback.apply(this, args);
           }, wait);
       }
   }
};

// 节流
export const throttle = (callback, wait) => {
    let timer = null;
    return function() {
        let args = arguments;
        if(!timer) {
            timer = setTimeout(() => {
                callback.apply(this, args);
                timer = null;
            }, wait);
        }
    }
}

// 合并对象
export const merge = (target) => {
    for (let i = 1, j = arguments.length; i < j; i++) {
        let source = arguments[i] || {};
        for (let prop in source) {
            if (source.hasOwnProperty(prop)) {
                let value = source[prop];
                if (value !== undefined) {
                    target[prop] = value;
                }
            }
        }
    }
    return target
}

// 克隆
export const clone = (obj, newObj = {}) => {

    // 对传进来的数据进行限制
    if(!(typeof obj == "function" || typeof obj == "object")){
        throw new Error(`obj must be a function or object`);
    }
    // 如果传进来的是个空对象或者空数组  则直接返回
    if(!(obj == undefined || obj == null)){
        if(Object.keys(obj).length == 0) return obj instanceof Array ? [] : {};
    } else {
        return;
    }
    // 遍历传进来的obj
    for (const key in obj) {
        // 判断当前属性是否是对象的属性（也就是不克隆原型上的属性）
        if (obj.hasOwnProperty(key)) { 
            const element = obj[key];

            // 判断当前属性值是引用数据类型还是基本数据类型
            if(typeof element == "function" || typeof element == "object"){

                // 进一步判断是那种引用类型然后进行赋值
                // 使用toString()
                let tostring = Object.prototype.toString;
                if(toString.call(obj[key]) == "[object Object]"){ // object类型
                    newObj[key] = new Object({});
                } else if (tostring.call(obj[key]) == "[object RegExp]"){ // 正则类型
                    newObj[key] = new RegExp();
                } else if (tostring.call(obj[key]) == "[object Array]"){ // 数组类型
                    newObj[key] = []
                }

                // 递归，当有数组或者对象的时候继续往里面深入，重复调用当前的函数
                clone(obj[key], newObj[key]);
            } else {
                // 原始数据类型直接赋值即可
                newObj[key] = element;
            }

        }
    }
    return newObj;
}

// 滚动当前页面到指定元素位置
// 楼层滚动
export const scrollElement = function(el) {
    document.getElementById(el).scrollIntoView({
        behavior: "smooth"
    })
}

// 获取对象key
const getObjKeys = function(obj) {
    return Object.getOwnPropertyNames(obj)
}

// 获取对象长度
export const getObjKeysSize = function(obj) {
    return getObjKeys(obj).length
}

// 获取当前时间的前num分钟  返回一个数组
export const getCurrRangeMinutes = function(n) {
    n = n || 10;
    let l = [];
    let i = new Idate(new Date());
    let ch = i.getHours();
    let cm = i.getMinutes();
    let h = 0;
    let m = 0;
    for(let j = 0, k = 0; j < n; j++) {
        m = (cm>>0) - j - 2;
        if(m > 0) {
            l.push(ch + ":" + completion(m, 2))
        } else {
            h = (ch>>0) - 1;
            m = 59 - k;
            l.push(completion(h, 2) + ":" + completion(m, 2));
            k++;
        }
    }
    return l.reverse();
}

// 获取当前时间的前num天  返回数组
export const getCurrRangeDate = function(n) {
    n = n || 8;
    let l = [];
    let i = new Idate(new Date());
    let cm = i.getMonth();
    let cd = i.getDate();
    let cy = i.getYear();
    let m = 0;
    let d = 0;
    let y = 0;
    for(let j = 0, k = 0; j < n; j++) {
        d = (cd>>0) - j - 1;
        if(d > 0) {
            l.push(cy + "-" + cm + "-" + completion(d, 2));
        } else {
            m = (cm>>0) - 1 <= 0 ? 12 : (cm>>0) - 1;
            y = (cm>>0) - 1 <= 0 ? (cy>>0) - 1 : cy;
            if(learYearMonth.includes(m)) d = 31;
            else if(m == 2) d = i.isLeapYear(y) ? 29 : 28;
            else d = 30;
            l.push(y + "-" + completion(m, 2) + "-" + completion(d - k, 2))
            k++;
        }
    }
    return l.reverse();
}