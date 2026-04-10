package com.qsh.aicodehelper.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码分析工具
 *
 * 提供代码复杂度分析、函数信息提取等功能的工具类
 * 支持作为AI工具的集成，为AI助手提供代码分析能力
 *
 * 主要功能：
 * - 代码复杂度分析（行数、圈复杂度、难度评估）
 * - 函数/方法签名提取
 * - 代码质量指标计算
 *
 * 技术关键点：
 * - @Component: Spring组件注解
 * - @Tool: LangChain4j工具注解，使方法可被AI调用
 * - Builder模式: 用于构建复杂对象
 * - 正则表达式: 用于代码模式匹配
 * - 圈复杂度算法: 评估代码复杂度
 */
@Component
public class CodeAnalysisTool {

    /**
     * 分析代码复杂度并提供度量指标
     *
     * 计算代码的各种复杂度指标，帮助评估代码质量和维护难度
     *
     * @param code 待分析的代码
     * @return CodeMetrics对象，包含代码的各项度量指标
     *
     * 技术关键点：
     * - @Tool注解：将此方法暴露为AI可调用工具
     * - 多维度分析：行数、圈复杂度、注释情况、难度评估
     * - Builder模式：构建复杂的度量结果对象
     * - 算法实现：包含多种代码分析算法
     */
    @Tool("Analyzes code complexity and provides metrics")
    public CodeMetrics analyzeCodeComplexity(String code) {
        return CodeMetrics.builder()
                .linesOfCode(countLines(code))                    // 代码行数统计
                .cyclomaticComplexity(calculateCyclomaticComplexity(code)) // 圈复杂度计算
                .hasComments(hasComments(code))                  // 注释检查
                .estimatedDifficulty(estimateDifficulty(code))  // 难度评估
                .build();
    }

    /**
     * 从代码中提取函数/方法签名信息
     *
     * 使用正则表达式匹配和提取指定函数的签名信息，包括返回类型、参数和可见性
     *
     * @param code 源代码
     * @param functionName 要提取的函数名
     * @return FunctionInfo对象，包含函数的详细信息
     *
     * 技术关键点：
     * - 正则表达式：复杂的模式匹配，支持Java方法签名
     * - Pattern.quote(): 转义函数名中的特殊字符
     * - 分组提取：使用正则分组提取不同类型的信息
     * - 错误处理：当找不到匹配时返回unknown信息
     */
    @Tool("Extracts function/method signatures from code")
    public FunctionInfo extractFunctionInfo(String code, String functionName) {
        // 构建正则表达式模式，匹配Java方法签名
        // 支持：访问修饰符(可选) + 返回类型 + 方法名 + 参数列表
        Pattern pattern = Pattern.compile(
                "(public|private|protected|static)?\\s*" +  // 访问修饰符（可选）
                "(\\w+)\\s+" +                            // 返回类型
                Pattern.quote(functionName) +               // 方法名（转义特殊字符）
                "\\s*\\(([^)]*)\\)"                      // 参数列表
        );

        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return FunctionInfo.builder()
                    .returnType(matcher.group(2))  // 提取返回类型
                    .parameters(matcher.group(3))  // 提取参数列表
                    .visibility(matcher.group(1) != null ? matcher.group(1).trim() : "package-private") // 提取可见性
                    .build();
        }

        // 未找到匹配时返回默认值
        return FunctionInfo.builder()
                .returnType("unknown")
                .parameters("unknown")
                .visibility("unknown")
                .build();
    }

    /**
     * 统计代码行数
     *
     * @param code 源代码
     * @return 代码行数
     *
     * 技术关键点：
     * - code.lines(): Java 8+的字符串行流API
     * - count(): 统计流中元素数量
     */
    private int countLines(String code) {
        return (int) code.lines().count();
    }

    /**
     * 计算圈复杂度（Cyclomatic Complexity）
     *
     * 圈复杂度是衡量代码复杂度的指标，数值越高表示代码越复杂、越难维护
     * 计算公式：基础复杂度1 + 控制流关键字数量
     *
     * @param code 源代码
     * @return 圈复杂度值
     *
     * 技术关键点：
     * - 控制流关键字：if, else, while, for, case, catch等
     * - 逻辑运算符：&&, ||, ?:
     * - 基础复杂度：任何代码至少有复杂度1
     * - 正则匹配：查找代码中的复杂度关键字
     */
    private int calculateCyclomaticComplexity(String code) {
        // 定义增加复杂度的关键字和运算符
        String[] complexityKeywords = {"if", "else", "while", "for", "case", "catch", "&& ", "|| ", "?:"};
        int complexity = 1; // 基础复杂度为1

        // 遍历每个关键字，统计出现次数
        for (String keyword : complexityKeywords) {
            Pattern pattern = Pattern.compile(keyword);
            Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                complexity++;
            }
        }

        return complexity;
    }

    /**
     * 检查代码是否包含注释
     *
     * @param code 源代码
     * @return true表示包含注释，false表示不包含
     *
     * 技术关键点：
     * - 支持多种注释格式：// 单行注释, /* 多行注释
     * - 简单检测：使用contains方法快速检查
     */
    private boolean hasComments(String code) {
        return code.contains("//") || code.contains("/*") || code.contains("*");
    }

    /**
     * 评估代码难度级别
     *
     * 基于圈复杂度和代码行数综合评估代码难度
     *
     * @param code 源代码
     * @return 难度级别：Low, Medium, High
     *
     * 评估标准：
     * - High: 圈复杂度 > 10 或 行数 > 100
     * - Medium: 圈复杂度 > 5 或 行数 > 50
     * - Low: 其他情况
     *
     * 技术关键点：
     * - 多维度评估：结合复杂度和规模
     * - 阈值设定：基于行业经验的合理阈值
     */
    private String estimateDifficulty(String code) {
        int complexity = calculateCyclomaticComplexity(code);
        int lines = countLines(code);

        if (complexity > 10 || lines > 100) {
            return "High";    // 高难度：复杂或规模大
        } else if (complexity > 5 || lines > 50) {
            return "Medium";  // 中等难度：适度复杂或规模
        } else {
            return "Low";     // 低难度：简单且规模小
        }
    }

    // ===========================================================================
    // 数据传输对象 (DTO)
    // ===========================================================================

    /**
     * 代码度量指标数据传输对象
     *
     * 封装代码分析的各类度量指标，使用Builder模式构建
     * 提供代码复杂度、规模和质量的相关信息
     */
    public static class CodeMetrics {
        private int linesOfCode;           // 代码行数
        private int cyclomaticComplexity;  // 圈复杂度
        private boolean hasComments;       // 是否包含注释
        private String estimatedDifficulty; // 预估难度级别

        /**
         * 创建Builder实例
         *
         * @return 新的Builder实例
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder模式实现类
         *
         * 提供流畅的API来构建CodeMetrics对象
         * 技术关键点：
         * - 链式调用：每个setter方法返回this
         * - 不可变性：build()方法返回不可变对象
         */
        public static class Builder {
            private CodeMetrics metrics = new CodeMetrics();

            public Builder linesOfCode(int linesOfCode) {
                metrics.linesOfCode = linesOfCode;
                return this;
            }

            public Builder cyclomaticComplexity(int cyclomaticComplexity) {
                metrics.cyclomaticComplexity = cyclomaticComplexity;
                return this;
            }

            public Builder hasComments(boolean hasComments) {
                metrics.hasComments = hasComments;
                return this;
            }

            public Builder estimatedDifficulty(String estimatedDifficulty) {
                metrics.estimatedDifficulty = estimatedDifficulty;
                return this;
            }

            public CodeMetrics build() {
                return metrics;
            }
        }

        // Getter方法
        public int getLinesOfCode() { return linesOfCode; }
        public int getCyclomaticComplexity() { return cyclomaticComplexity; }
        public boolean isHasComments() { return hasComments; }
        public String getEstimatedDifficulty() { return estimatedDifficulty; }
    }

    /**
     * 函数信息数据传输对象
     *
     * 封装函数的签名信息，包括返回类型、参数列表和访问可见性
     * 用于代码分析和重构工具
     */
    public static class FunctionInfo {
        private String returnType;   // 函数返回类型
        private String parameters;   // 函数参数列表
        private String visibility;   // 访问修饰符（public, private等）

        /**
         * 创建Builder实例
         *
         * @return 新的Builder实例
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder模式实现类
         *
         * 提供流畅的API来构建FunctionInfo对象
         */
        public static class Builder {
            private FunctionInfo info = new FunctionInfo();

            public Builder returnType(String returnType) {
                info.returnType = returnType;
                return this;
            }

            public Builder parameters(String parameters) {
                info.parameters = parameters;
                return this;
            }

            public Builder visibility(String visibility) {
                info.visibility = visibility;
                return this;
            }

            public FunctionInfo build() {
                return info;
            }
        }

        // Getter方法
        public String getReturnType() { return returnType; }
        public String getParameters() { return parameters; }
        public String getVisibility() { return visibility; }
    }
}